package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    //Constante q define el tamaño max permitido para los archivos (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    //Constante para definir los tipo de archivos admitidos
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};
    //Cliente de Cloudinary inyectando como dependencia
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Subir imgs a la raiz de Cloudinary
     * @param file
     * @return UNRL de la img
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException{
        //1.Validamos el archivo
        validateImage(file);

        //Sube el archivo a Cloudinary con configuraciones básicas
        //Tipo de recurso auto-detectado
        //Calidad automática con nivel "good"
        Map<?, ?> uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto:good"
                ));

        //Retorna la URL segura de la img
        return (String) uploadResult.get("secure_url");
    }


    /**
     * Sube una img a una carpeta en específico
     * @param file
     * @param folder carpeta destino
     * @return URL segura (HTTPS) de la img subida
     * @throws IOException Si ocurre un error durante la subida
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException{
        validateImage(file);
        //Generar un nombre único para el archivo
        //Conservar la extensión original
        //Agregar un prefijo y un UUID para evitar colisiones

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFileName = "img_" + UUID.randomUUID() + fileExtension;

        //Configuración para subir img
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder,       //Carpeta de destino
                "public_id", uniqueFileName,    //Nombre único para el archivo
                "use_filename", false,          //No usar el nombre original
                "unique_filename", false,       //No generar nombre único (proceso hecho anteriormente)
                "overwrite", false,             //No sobreescribir archivos
                "resource_type", "auto",        //Auto-detectar tpo de recurso
                "quality", "auto:good"          //Optimización de calidad automática
        );

        //Subir el archivo
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),options);
        //Retornamos la URL segura
        return (String) uploadResult.get("secure_url");
    }


    /**
     *
     * @param file
     */
    private void validateImage(MultipartFile file){
        //1. Verificar si el archivo está vacío
        if (file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        //2. Verificar el tamaño de la img
        if (file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("El archivo no puede ser mayor a 5MB");
        }

        //3. Obtener y validar el nombre original del archivo
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null){
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        //4. Extraer y validar la extensión
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)){
            throw new IllegalArgumentException("Solo se permiten archivos JPG, JEPG y PNG");
        }

        //Verifica que el tipo de MIME sea una img
        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("El archivo debe ser una imagen válida.");
        }

    }
}