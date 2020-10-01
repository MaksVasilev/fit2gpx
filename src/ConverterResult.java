import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class ConverterResult {

    static ResourceBundle tr = ResourceBundle.getBundle("locale/tr", Locale.getDefault());

    private final ArrayList<String> GoodFiles = new ArrayList<>();
    private final ArrayList<String> EmptyFiles = new ArrayList<>();
    private final ArrayList<String> BadFiles = new ArrayList<>();

    public int getGoodFilesCount() {return GoodFiles.size();}
    int getEmptyFilesCount() {return EmptyFiles.size();}
    int getBadFilesCount() {return BadFiles.size();}

    void add(int result, String file) {
        if(result == 0) {GoodFiles.add(file);}
        if(result == 200 || result == 201) {EmptyFiles.add(file);}
        if(result == 65) {BadFiles.add(file + tr.getString("_file_corrupt"));}
        if(result == 66) {BadFiles.add(file + tr.getString("_file_not_found"));}
        if(result == 73) {BadFiles.add(file + tr.getString("_file_save_error"));}
        if(result == 81) {BadFiles.add(file + tr.getString("_file_not_to_db"));}
        if(result == 89) {BadFiles.add(file + tr.getString("_file_not_data"));}
        if(result == 199) {BadFiles.add(file + tr.getString("_read_data_from_file_error"));}
    }

    String getSummaryByString() {

        StringBuilder result = new StringBuilder(tr.getString("FilesParcedOk_") + GoodFiles.size());
        if(GoodFiles.size() < 11) {
            for (String GoodFile : GoodFiles) {
                result.append("\n    ").append(GoodFile);
            }
        } else {
            for(int g = 0; g < 11; g++) {
                result.append("\n    ").append(GoodFiles.get(g));
            }
            result.append("\n  ").append(tr.getString("_more_files_")).append((GoodFiles.size() - 10));
        }

        result.append("\n\n").append(tr.getString("FilesNoTrack_")).append(EmptyFiles.size());
        if(EmptyFiles.size() < 11) {
            for (String EmptyFile : EmptyFiles) {
                result.append("\n    ").append(EmptyFile);
            }
        } else {
            for(int g = 0; g < 11; g++) {
                result.append("\n    ").append(EmptyFiles.get(g));
            }
            result.append("\n  ").append(tr.getString("_more_files_")).append((EmptyFiles.size() - 10));
        }

        result.append("\n\n").append(tr.getString("FilesWithError_")).append(BadFiles.size());
        if(BadFiles.size() < 11) {
            for (String BadFile : BadFiles) {
                result.append("\n    ").append(BadFile);
            }
        } else {
            for(int g = 0; g < 11; g++) {
                result.append("\n    ").append(BadFiles.get(g));
            }
            result.append("\n  ").append(tr.getString("_more_files_")).append((BadFiles.size() - 10));
        }

        return result.toString();
    }

}
