package carga.demo.modelo;

import lombok.Getter;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ResponseWrapper {
    private boolean successful;
    private String message;
    private Object data;
    private Map<String, String> errors;

    // Constructor por defecto
    public ResponseWrapper() {
        errors = new HashMap<>();
    }

    // Constructor para éxito o error con datos
    public ResponseWrapper(boolean successful, String message, Object data) {
        this.successful = successful;
        this.message = message;
        this.data = data;
        this.errors = new HashMap<>();
    }

    // Constructor para manejo de errores
    public ResponseWrapper(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
        this.errors = new HashMap<>();
    }

    // Método para agregar un error específico
    public void addError(String key, String value) {
        errors.put(key, value);
    }

    // Método estático para simplificar respuestas de error
    public static ResponseWrapper error(String message) {
        ResponseWrapper response = new ResponseWrapper();
        response.setSuccessful(false);
        response.setMessage(message);
        response.setData(null); // Asegúrate de que 'data' no sea null
        response.setErrors(new HashMap<>()); // Inicializa el mapa de errores
        return response;
    }

    // Método estático para simplificar respuestas exitosas
    public static ResponseWrapper success(String message, Object data) {
        ResponseWrapper response = new ResponseWrapper();
        response.setSuccessful(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
