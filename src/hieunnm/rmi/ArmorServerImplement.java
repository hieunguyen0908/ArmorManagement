package hieunnm.rmi;

import hieunnm.dtos.ArmorDTO;
import hieunnm.rmi.ArmorInterface;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ArmorServerImplement extends UnicastRemoteObject implements ArmorInterface {

    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String fileName = "ArmorData.txt";
    private FileWriter fw;
    private BufferedWriter bw;
    private FileReader fr;
    private BufferedReader br;

    public ArmorServerImplement() throws RemoteException {

    }

    @Override
    public boolean createArmor(ArmorDTO dto) {
        boolean addNew = true;
        List<ArmorDTO> listArmors = showAllArmor();
        for (ArmorDTO armor : listArmors) {
            if (armor.getId().equals(dto.getId())) {
                return false;
            }
        }

        try {
            fw = new FileWriter(fileName, true);
            bw = new BufferedWriter(fw);
            bw.append(dto.getId() + "; " + dto.getClassification() + "; "
                    + dto.getDescription() + "; " + dto.getStatus() + "; "
                    + new SimpleDateFormat(DATE_FORMAT).format(dto.getTimeOfCreate())
                    + "; " + dto.getDefense());
            bw.newLine();
            addNew = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            closeFileStream();
        }
        return addNew;
    }

    @Override
    public ArmorDTO findByArmorID(String id) {
        ArmorDTO dto = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] armor = line.split("; ");
                dto = new ArmorDTO();
                dto.setId(armor[0]);
                dto.setClassification(armor[1]);
                dto.setDescription(armor[2]);
                dto.setStatus(armor[3]);
                dto.setTimeOfCreate(new SimpleDateFormat(DATE_FORMAT).parse(armor[4]));
                dto.setDefense(Integer.parseInt(armor[5]));
                if (dto.getId().equals(id)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading database file.");
        } catch (NumberFormatException e) {
            System.out.println("Armor defense is invalid.");
        } catch (ParseException e) {
            System.out.println("Armor timeOfCreate is invalid.");
        } finally {
            closeFileStream();
        }
        if (dto != null) {
            if (dto.getId().equals(id)) {
                return dto;
            }
        }
        return null;
    }

    @Override
    public List<ArmorDTO> showAllArmor() {
        List<ArmorDTO> list = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String line;
            list = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] armor = line.split("; ");
                ArmorDTO dto = new ArmorDTO();
                dto.setId(armor[0]);
                dto.setClassification(armor[1]);
                dto.setDescription(armor[2]);
                dto.setStatus(armor[3]);
                dto.setTimeOfCreate(new SimpleDateFormat(DATE_FORMAT).parse(armor[4]));
                dto.setDefense(Integer.parseInt(armor[5]));
                list.add(dto);
            }
        } catch (IOException e) {
            System.out.println("Error while reading database file.");
        } catch (NumberFormatException e) {
            System.out.println("Armor defense is invalid.");
        } catch (ParseException e) {
            System.out.println("Armor timeOfCreate is invalid.");
        } finally {
            closeFileStream();
        }
        return list;
    }

    @Override
    public boolean removeArmor(String id) {
        boolean check = false;
        try {
            List<ArmorDTO> armorList = showAllArmor();
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < armorList.size(); i++) {
                ArmorDTO armorInList = armorList.get(i);
                if (armorInList.getId().equals(id)) {
                    continue;
                }
                bw.write(armorInList.getId() + "; " + armorInList.getClassification() + "; "
                        + armorInList.getDescription() + "; " + armorInList.getStatus() + "; "
                        + new SimpleDateFormat(DATE_FORMAT).format(armorInList.getTimeOfCreate())
                        + "; " + armorInList.getDefense());
                bw.newLine();
            }
            check = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            closeFileStream();
        }
        return check;
    }

    @Override
    public boolean updateArmor(ArmorDTO dto) {
        boolean check = false;
        try {
            List<ArmorDTO> listArmor = showAllArmor();
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < listArmor.size(); i++) {

                ArmorDTO armorInList = listArmor.get(i);
                if (armorInList.getId().equals(dto.getId())) {
                    armorInList.setClassification(dto.getClassification());
                    armorInList.setDefense(dto.getDefense());
                    armorInList.setDescription(dto.getDescription());
                    armorInList.setStatus(dto.getStatus());
                    armorInList.setTimeOfCreate(dto.getTimeOfCreate());
                }
                bw.write(armorInList.getId()+ "; " + armorInList.getClassification() + "; "
                        + armorInList.getDescription() + "; " + armorInList.getStatus() + "; "
                        + new SimpleDateFormat(DATE_FORMAT).format(armorInList.getTimeOfCreate())
                        + "; " + armorInList.getDefense());
                bw.newLine();
            }
            check = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            closeFileStream();
        }
        return check;
    }

    public void closeFileStream() {
        try {
            if (bw != null) {
                bw.close();
            }
            if (fw != null) {
                fw.close();
            }
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {

        }
    }

}
