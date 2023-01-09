package fr.lernejo.navy_battle;
import java.util.*;

public class Map {

    public List<List<String>> boatList = new ArrayList<>();
    public char[][] globalMap = null;
    private final ToolMethod toolMethod = new ToolMethod();

    public Map() throws InterruptedException {
    }

    public void flushMap(){
        List<Integer> sizeList = Arrays.asList(5,4,3,3,2);
        createMap();
        for (Integer b: sizeList) {
            choosePosition(b);}
    }

    public List<String> computeResult(String targetCell){
        List<String> res = new ArrayList<>();
        List<Integer> targetCellInfo = toolMethod.FromCaseToList(targetCell);
        if (globalMap[targetCellInfo.get(0)][targetCellInfo.get(1)] == 'X'){
            for (List<String> posList: boatList) {
                if (posList.contains(targetCell)){
                    posList.remove(targetCell);
                    if (posList.toArray().length == 0){
                        boatList.remove(posList);
                        res.add("sunk");
                        if (boatList.toArray().length == 0)     {res.add("false");}
                        else     {res.add("true");}
                        break;
                    }
                    else
                    {
                        res.add("hit");
                        res.add("true");
                        break;
                    }}}}
        else {
            res.add("miss");
            res.add("true");
        }
        return res;
    }

    public void createMap(){
        char[][] map = new char[10][10];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                map[i][j] = '0';
            }}
        globalMap = map;
    }

    public boolean isPositionOk(Integer size, String position){
        if (toolMethod.FromCaseToList(position).get(1) + size > 10){
            return false;
        }
        boolean valid = true;
        for (List<String> m: boatList) {
            for (String v: m) {
                for (int i = 0; i < size; i++) {
                    List<Integer> tryPosInfo = toolMethod.FromCaseToList(position);
                    String triedPos = toolMethod.FromListToCase(Arrays.asList(tryPosInfo.get(0), tryPosInfo.get(1)  + i));
                    if (Objects.equals(triedPos, v)){
                        return false;
                    }}}}
        return valid;
    }

    public void choosePosition(Integer boat){
        String place = toolMethod.chooseCase();
        boolean isPosOk = isPositionOk(boat, place);
        while (!isPosOk) {
            place = toolMethod.chooseCase();
            isPosOk = isPositionOk(boat, place);
        }
        List<Integer> placeInfo = toolMethod.FromCaseToList(place);
        int column = placeInfo.get(0);
        int row = placeInfo.get(1);
        List<String> positionList = new ArrayList<>();
        for (int i = 0; i < boat; i++) {
            List<Integer> info = Arrays.asList((column), (row+i));
            positionList.add(toolMethod.FromListToCase(info));
            globalMap[info.get(0)][info.get(1)] = 'X';
        }
        boatList.add(positionList);
    }
}
