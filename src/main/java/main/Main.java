package main;

import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;
import javax.servlet.MultipartConfigElement;


public class Main {
	
	private static final String PATH_IMAGES="C:\\data\\imagenes";
	
	public static void main(String args[]) {
		System.out.println("********* API Iniciada ******");
		
		port(8090);
		
		post("/upload", new Route() {
		    public Object handle(Request request, Response response) throws Exception {
		        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

		        InputStream input = null;
		        FileOutputStream output = null;

		        try {
		            String fileName = request.raw().getPart("image").getSubmittedFileName();
		            File uploadedFile = new File(PATH_IMAGES + File.separator + fileName);

		            input = request.raw().getPart("image").getInputStream();
		            output = new FileOutputStream(uploadedFile);

		            byte[] buffer = new byte[1024];
		            int bytesRead;
		            while ((bytesRead = input.read(buffer)) != -1) {
		                output.write(buffer, 0, bytesRead);
		            }

		            response.status(201); // Código 201: Created
		            return "Imagen subida correctamente: " + fileName;
		        } finally {
		            if (input != null) {
		                try {
		                    input.close();
		                } catch (Exception e) {
		                	System.out.println(e);
		                    e.printStackTrace();
		                }
		            }
		            if (output != null) {
		                try {
		                    output.close();
		                } catch (Exception e) {
		                	System.out.println(e);
		                    e.printStackTrace();
		                }
		            }
		        }
		    }
		});

		
		get("/imagenes", new Route() {
			public Object handle(Request request, Response response) throws Exception {
				try {
					List <Imagen> imagenes = getImageFiles(PATH_IMAGES);
					return new Gson().toJson(ResponseGeneric.success(imagenes)); 
				} catch (Exception e) {
					return ResponseGeneric.error(e.getMessage());
				}
				
			}
		});
		
		get("/imagenes/:nombre", new Route() {
		    public Object handle(Request request, Response response) throws Exception {
		        String nombreImagen = request.params(":nombre");
		        File imagen = new File(PATH_IMAGES, nombreImagen);

		        if (imagen.exists() && (imagen.getName().toLowerCase().endsWith(".png") || imagen.getName().toLowerCase().endsWith(".jpg"))) {
		            response.raw().setContentType("image/jpeg"); // Cambia a "image/png" si es necesario
		            response.raw().setContentLengthLong(imagen.length());

		            FileInputStream fis = null;
		            OutputStream os = null;
		            try {
		                fis = new FileInputStream(imagen);
		                os = response.raw().getOutputStream();

		                byte[] buffer = new byte[1024];
		                int read;
		                while ((read = fis.read(buffer)) != -1) {
		                    os.write(buffer, 0, read);
		                }
		                os.flush();
		            } finally {
		                if (fis != null) {
		                    try {
		                        fis.close();
		                    } catch (Exception e) {
		                        e.printStackTrace();
		                    }
		                }
		                if (os != null) {
		                    try {
		                        os.close();
		                    } catch (Exception e) {
		                        e.printStackTrace();
		                    }
		                }
		            }

		            return response.raw();
		        } else {
		            response.status(404);
		            return "Imagen no encontrada o no es un tipo de imagen válido.";
		        }
		    }
		});
		
	}
	
	private static List<Imagen> getImageFiles(String directoryPath) {
        List<Imagen> imagenes = new ArrayList();
        File directory = new File(directoryPath);

        if (directory.isDirectory()) {
            File[] files = directory.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg");
				}
			});
            if (files != null) {
                for (File file : files) {
                	Imagen imagen = new Imagen();
                	imagen.setNombre(file.getName());
                	imagen.setUrl("http://localhost:8090/imagenes/"+file.getName());
                	imagen.setPath(file.getAbsolutePath());
                	imagenes.add(imagen);
                }
            }
        } else {
            System.out.println("La ruta proporcionada no es un directorio.");
        }

        return imagenes;
    }

	
}
