import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class People {
    public final String number;
    public String name;
    public String date;
    public HashMap<String, String> data = new HashMap<>();

    public People(String i) {
        number = i;
        data.put("number",number);
    }

    public String contains(String in) {
        String found = "false";
        Set search = data.keySet();
        Iterator<String> iterator = search.iterator();
        while (iterator.hasNext()) {
            String read = iterator.next();
            if (in.contains(read)) {
                found = read;
                break;
            }
        }
        return found;
    }

}
