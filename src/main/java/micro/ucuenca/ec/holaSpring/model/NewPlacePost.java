package micro.ucuenca.ec.holaSpring.model;

import org.springframework.web.multipart.MultipartFile;

public class NewPlacePost {

        private String model;
        private MultipartFile[] files;


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public MultipartFile[] getFiles() {
        return files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
