package tools;

import java.io.File;

public class FileCheck {
    private File fc;

    public boolean FileCanRead(String file) {     // проверка существования и доступности файла
        fc = new File(file);
        return fc.exists() && fc.isFile() && fc.canRead();
    }

    public boolean FileCanWrite(String file) {     // проверка существования и доступности файла
        fc = new File(file);
        return fc.exists() && fc.isFile() && fc.canWrite();
    }

    public String FullPath(String file){
        fc = new File(file);
        if(fc.exists()) {

            return fc.getAbsolutePath();
        } else return "";
    }


}
