package IntegracionBackFront.backfront.Config.Cloudinary;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    //Variables para almacenar las credenciales de cloudinary
    private String cloudName;
    private String apiKey;
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary(){
        //Crear un obj de tipo dotenv
        Dotenv dotenv = Dotenv.load();

        //Crear un Map para guardar la clave valor del archivo .emv
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"));      //Nombre de la nube
        config.put("api_key", dotenv.get("CLOUDINARY_API_KEY"));            //API Key para autenticacion
        config.put("api_secret", dotenv.get("CLOUDINARY_API_SECRET"));

        //Retorna una nueva instancia de Claudinary con la configuración cargada
        return new Cloudinary(config);
    }
}
