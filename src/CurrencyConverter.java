import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

// Modelo de datos para mapear la respuesta JSON
class ExchangeRatesResponse {
    public String base; // Moneda base
    public Map<String, Double> rates; // Tasas de cambio
}

public class CurrencyConverter {
    private static final String API_KEY = "c6754d87cba9eed9b0162c88"; // Reemplaza con tu clave de API
    private static final String BASE_URL = "https://api.exchangerate-api.com/v4/latest/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar al usuario la moneda base
        System.out.println("Bienvenido al conversor de monedas.");
        System.out.print("Por favor, ingrese el código de la moneda base (ejemplo: MXN, USD, EUR): ");
        String monedaBase = scanner.nextLine().toUpperCase();

        boolean continuar = true;

        while (continuar) {
            System.out.println("Seleccione la moneda a la que desea convertir:");
            System.out.println("1. USD");
            System.out.println("2. EUR");
            System.out.println("3. GBP");
            System.out.println("4. JPY");
            System.out.println("5. Cambiar moneda base");
            System.out.println("6. Salir");
            System.out.print("Ingrese su elección: ");
            int opcion = scanner.nextInt();

            if (opcion == 6) {
                continuar = false;
                System.out.println("¡Gracias por usar el conversor de monedas!");
                break;
            }

            if (opcion == 5) {
                System.out.print("Ingrese el nuevo código de la moneda base: ");
                scanner.nextLine(); // Consumir el salto de línea
                monedaBase = scanner.nextLine().toUpperCase();
                continue;
            }

            System.out.print("Ingrese la cantidad en su moneda base (ejemplo: 100): ");
            double cantidadBase = scanner.nextDouble();

            String monedaDestino = "";
            switch (opcion) {
                case 1:
                    monedaDestino = "USD";
                    break;
                case 2:
                    monedaDestino = "EUR";
                    break;
                case 3:
                    monedaDestino = "GBP";
                    break;
                case 4:
                    monedaDestino = "JPY";
                    break;
                default:
                    System.out.println("Opción no válida.");
                    continue;
            }

            double tasaCambio = obtenerTasaCambio(monedaBase, monedaDestino);
            if (tasaCambio != -1) {
                double resultado = cantidadBase * tasaCambio;
                System.out.printf("La cantidad %.2f %s es equivalente a %.2f %s.\n", cantidadBase, monedaBase, resultado, monedaDestino);
            }
        }

        scanner.close();
    }

    private static double obtenerTasaCambio(String monedaBase, String monedaDestino) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = BASE_URL + monedaBase + "?access_key=" + API_KEY;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            ExchangeRatesResponse ratesResponse = gson.fromJson(response.body(), ExchangeRatesResponse.class);

            if (ratesResponse.rates.containsKey(monedaDestino)) {
                return ratesResponse.rates.get(monedaDestino);
            } else {
                System.out.println("No se encontró la moneda de destino.");
                return -1;
            }
        } catch (Exception e) {
            System.out.println("Error al obtener la tasa de cambio: " + e.getMessage());
            return -1;
        }
    }
}
