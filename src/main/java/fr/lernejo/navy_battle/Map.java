package fr.lernejo.navy_battle;
import java.util.*;

public class Map {

    public final ToolMethod toolMethod = new ToolMethod();

    public Map() throws InterruptedException {
    }
    public List<Object> flushMap(){
        char[][] globalMap = createMap();
        List<List<String>> boatList = new ArrayList<>();
        List<Integer> sizeList = Arrays.asList(5,4,3,3,2);
        createMap();
        for (Integer b: sizeList) {
            choosePosition(b, boatList, globalMap);}
        List<Object> stupidReturn = new ArrayList<>();
        stupidReturn.add(globalMap);
        stupidReturn.add(boatList);
        return stupidReturn;
    }
    public List<String> computeResult(String targetCell, List<List<String>> boatList, char[][] globalMap){
        List<String> res = new ArrayList<>();
        List<Integer> targetCellInfo = toolMethod.FromCaseToList(targetCell);
        if (globalMap[targetCellInfo.get(0)][targetCellInfo.get(1)] == 'X'){
                res = bigLoop(targetCell, res, boatList);
            }
        else {
            res.add("miss");
            res.add("true");
        }
        return res;
    }
    public List<String> bigLoop(String targetCell, List<String> res, List<List<String>> boatList){
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
                else{res.add("hit");         res.add("true");
                    break;
                }}}
        return res;
    }
    public char[][] createMap(){
        char[][] map = new char[10][10];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                map[i][j] = '0';
            }}
        return map;
    }
    public boolean isPositionOk(Integer size, String position, List<List<String>> boatList){
        if (toolMethod.FromCaseToList(position).get(1) + size > 10){
            return false;}
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
    public void choosePosition(Integer boat, List<List<String>> boatList, char[][] globalMap){
        String place = toolMethod.chooseCase();
        boolean isPosOk = isPositionOk(boat, place, boatList);
        while (!isPosOk) {
            place = toolMethod.chooseCase();
            isPosOk = isPositionOk(boat, place, boatList);
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
    }}
