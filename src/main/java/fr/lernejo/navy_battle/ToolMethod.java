package fr.lernejo.navy_battle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
class ToolMethod{

    public List<Integer> FromCaseToList(String caseStr){return Arrays.asList(caseStr.charAt(0) - 'A', Integer.parseInt(caseStr.replace("" + caseStr.charAt(0), "")) - 1);}

    public String FromListToCase(List<Integer> caseInfo){return "" + ((char)(caseInfo.get(0) + 'A')) + (caseInfo.get(1)+1);}
    public String genId(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++)    {sb.append(RandomChar());}
        return sb.toString();
    }

    public char RandomChar(){
        Random rnd = new Random();
        if (rnd.nextInt(0,2) == 0){
            return ((char) rnd.nextInt(48, 57));
        }
        else if (rnd.nextInt(0,2) == 1){
            return ((char) rnd.nextInt(97, 122));
        }
        else {
            return ((char) rnd.nextInt(65, 90));

        }
    }

    public String chooseCase() {
        Random rnd = new Random();
        List<Character> columns = new ArrayList<>(); // A -> J
        List<Integer> rows = new ArrayList<>();  // 1 -> 10
        for (int i = 1; i < 11; i++)    {columns.add(((char) (64 + i)));}
        for (int i = 1; i < 11; i++)    {rows.add(i);}
        return String.valueOf(columns.get(rnd.nextInt(0, 10))) + rows.get(rnd.nextInt(0, 10));
    }
}

