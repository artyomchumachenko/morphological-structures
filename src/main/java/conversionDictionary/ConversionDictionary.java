/*
 * Copyright (C) 2017  Alexander Porechny alex.porechny@mail.ru
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0) as published by the Creative Commons.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * for more details.
 *
 * You should have received a copy of the Attribution-NonCommercial-ShareAlike
 * 3.0 Unported (CC BY-SA 3.0) along with this program.
 * If not, see <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Thanks to Sergey Politsyn and Katherine Politsyn for their help in the development of the library.
 *
 *
 * Copyright (C) 2017 Александр Поречный alex.porechny@mail.ru
 *
 * Эта программа свободного ПО: Вы можете распространять и / или изменять ее
 * в соответствии с условиями Attribution-NonCommercial-ShareAlike 3.0 Unported
 * (CC BY-SA 3.0), опубликованными Creative Commons.
 *
 * Эта программа распространяется в надежде, что она будет полезна,
 * но БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; без подразумеваемой гарантии
 * КОММЕРЧЕСКАЯ ПРИГОДНОСТЬ ИЛИ ПРИГОДНОСТЬ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.
 * См. Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-SA 3.0)
 * для более подробной информации.
 *
 * Вы должны были получить копию Attribution-NonCommercial-ShareAlike 3.0
 * Unported (CC BY-SA 3.0) вместе с этой программой.
 * Если нет, см. <https://creativecommons.org/licenses/by-nc-sa/3.0/legalcode>
 *
 * Благодарим Сергея и Екатерину Полицыных за оказание помощи в разработке библиотеки.
 */

package conversionDictionary;

import load.BDSqlite;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import load.FileHelper;

import static morphologicalstructures.Property.START_ID_INITIAL_SAVE;

public class ConversionDictionary {

    private static final byte[] CONTROL_VALUE;
    private static BufferedReader readerSourceDictionary;
    private static FileOutputStream streamKeyAndHashAndMorfCharacteristics;
//    private static HashMap<Integer, IdAndString> stringWordFormAndId;
//    private static HashMap<Integer, IdAndString> stringInitialFormAndId;
    private int idInitialSave = START_ID_INITIAL_SAVE;
    private static BDSqlite CONNECT_BD_INITIAL_FORM;
    private static BDSqlite CONNECT_BD_WORD_FORM;

    static {
        CONTROL_VALUE = getBytes(PropertyForConversion.CONTROL_VALUE);// new byte[]{-1, -1, -1, -1};
    }

    private ConversionDictionary() {}

    public static void conversionDictionary(String sourceDictionaryPath, String encoding) {
        initFiles(sourceDictionaryPath, encoding);
        conversionDictionary();
    }

    //TODO:PATH_KEY_HASH_AND_MORF_CHARACTERISTICS in ZIP
    private static void initFiles(String sourceDictionaryPath, String encoding) {
        readerSourceDictionary = FileHelper.openBufferedReaderStream(sourceDictionaryPath, encoding);
        streamKeyAndHashAndMorfCharacteristics = FileHelper.openFileInputStream(PropertyForConversion.PATH_KEY_HASH_AND_MORF_CHARACTERISTICS);
        CONNECT_BD_INITIAL_FORM = new BDSqlite(PropertyForConversion.PATH_BD_INITIAL_FORM);
        CONNECT_BD_WORD_FORM = new BDSqlite(PropertyForConversion.PATH_BD_WORD_FORM);
    }

    private static void conversionDictionary() {
        searchFirstLemma();

        /**
         * проводит в единный формат для конвертации
         * Написать метод, который распознает значение характеристики и переводит в шкалу.
         * Для начальных формы записать в БД, записать в файл в формате ключ от БД, хэш-код, часть речи характеристика
         * Для производный форм, проверить в мапе существует ли похожый хэш,
         * если сущесвтует, то прерить одинаковый ли стринг,
         *  если нет, то вывести в лог,
         *  если да, то записать файл в формате ключ в БД (берем из мапы) хэшкод, характиристика
         * если не сущевтует, то добавить в БД, добавить в мап где ключ это хэшкод а значение ключ в БД
         *  и записать в файл
         * проверить слово на йо, если да, то повторить операцию выше, но ключ берется тот же для слова, а не создается новый.
         *
         * повторят пока не пройдем все лемы
         *
         * закрыть все соединения
        **/
    }

    private static void searchFirstLemma() {
        while(!FileHelper.readLine(readerSourceDictionary).trim().equals("<lemmata>")){}
    }

    private static void conversionLemmas() {
        while(FileHelper.ready(readerSourceDictionary)) {
            String stringLemma = FileHelper.readLine(readerSourceDictionary);

        }
    }

    public static String conversionLemma(String oldLemma) {
        String newLemma = deleteTegs(oldLemma);
        newLemma = leadToClearStyle(newLemma);
        String[] arr = newLemma.split("</[fl]>");
        for(String line : arr) {
            System.out.println(line);
        }
        return newLemma;
    }

    private static String deleteTegs(String oldLemma) {
        String newLemma = oldLemma.toLowerCase();
        newLemma = newLemma.replaceAll("[ ]+", "");
        newLemma = newLemma.replaceAll("<lemmaid=\"[\\d]+\"rev=\"[\\d]+\">", "");
        newLemma = newLemma.replaceAll("</lemma>", "");
        newLemma = newLemma.replaceAll("[tv]=", "");
        newLemma = newLemma.replaceAll("[\"]", "");
        newLemma = newLemma.replaceAll("<[lfg]", "<");
        newLemma = newLemma.replaceAll("/>", ">");
        return newLemma;
    }

    private static String leadToClearStyle(String oldLemma) {
        String newLemma = oldLemma;
        return newLemma;
    }
//    private static HashMap<Integer, IdAndString> generateMapIdAndString(BufferedReader outReader) {
//        HashMap<Integer, IdAndString> mapStringAndId = new HashMap<>();
//        try {
//            int id = 0;
//            mapStringAndId.put(0, new IdAndString("??_??_???????", 0));
//            while (outReader.ready()) {
//                id++;
//                String word = outReader.readLine();
//                IdAndString stringAndID = new IdAndString(word, id);
//                mapStringAndId.put(stringAndID.hashCode(), stringAndID);
//                if(word.matches("ё")) {
//                    stringAndID = new IdAndString(word.replace("ё", "e"), id);
//                    mapStringAndId.put(stringAndID.hashCode(), stringAndID);
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return mapStringAndId;
//    }


//    private void saveInBD(String nameBD, HashMap<Integer, IdAndString> stringFormAndId) {
//        BDSqlite outBD = new BDSqlite(nameBD);
//        outBD.execute("CREATE TABLE if not exists 'Form' ('id' INTEGER NOT NULL, 'StringForm' TEXT NOT NULL, PRIMARY KEY('id'))");
//        saveStringAndIdInBD(stringFormAndId, outBD);
//        outBD.closeDB();
//    }

//    private void saveStringAndIdInBD(HashMap<Integer, IdAndString> stringFormAndId, BDSqlite outDBWordFormString) {

//        outDBWordFormString.execute("BEGIN TRANSACTION");
//        for (Object obj : stringFormAndId.values()) {
//            IdAndString idAndString = (IdAndString) obj;
//            outDBWordFormString.execute(String.format("INSERT INTO 'Form' ('id','StringForm') VALUES (%d, '%s'); ", idAndString.myId, idAndString.myString));
//        }
//        outDBWordFormString.execute("END TRANSACTION");
//    }

    private void saveInitialForm(String strForms) {
        String strInitialForm;
        if (strForms.contains("\"")) {
            strInitialForm = strForms.substring(0, strForms.indexOf("\""));
        } else {
            strInitialForm = strForms;
        }
        String[] initialFormParameters = strInitialForm.split(" ");

//        stringInitialFormAndId.put(0, new IdAndString("?????_??_???????", 0));
        try {
            idInitialSave++;
            streamKeyAndHashAndMorfCharacteristics.write(getBytes(initialFormParameters[0].hashCode()));
            streamKeyAndHashAndMorfCharacteristics.write(Byte.decode("0x" + initialFormParameters[1]));
            streamKeyAndHashAndMorfCharacteristics.write(getBytes(new BigInteger(initialFormParameters[2], 16).longValue()));
            streamKeyAndHashAndMorfCharacteristics.write(getBytes(idInitialSave));

//            IdAndString stringAndID = new IdAndString(initialFormParameters[0], idInitialSave);
//            stringInitialFormAndId.put(stringAndID.myId, stringAndID);
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            System.err.println("");
        }
    }

    private static byte[] getBytes(int value) {
        byte[] bytes = new byte[]{
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value)
        };
        return bytes;
    }

    private static byte[] getBytes(long value) {
        byte[] bytes = new byte[]{
            (byte) (value >> 56),
            (byte) (value >> 48),
            (byte) (value >> 40),
            (byte) (value >> 32),
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) (value)
        };
        return bytes;
    }

    private void saveForm(String strForm) {

        String[] wordlFormParameters = strForm.split(" ");
        int hashCodeForm = 0;
        try {
            hashCodeForm = wordlFormParameters[0].hashCode();
            streamKeyAndHashAndMorfCharacteristics.write(getBytes(hashCodeForm));
            streamKeyAndHashAndMorfCharacteristics.write(getBytes(new BigInteger(wordlFormParameters[1], 16).longValue()));
//            streamKeyAndHashAndMorfCharacteristics.write(getBytes(stringWordFormAndId.get(hashCodeForm).myId));
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, strForm, ex);
        }
    }

    private void saveEndLemma() {
        try {
            streamKeyAndHashAndMorfCharacteristics.write(CONTROL_VALUE);
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeFiles() {
        FileHelper.closeFile(readerSourceDictionary);
        FileHelper.closeFile(streamKeyAndHashAndMorfCharacteristics);
    }

    public static void createdInitianAndWordFormString() {
        BufferedReader inDictionary = FileHelper.openBufferedReaderStream("dictionary.format.number.txt", PropertyForConversion.ENCODING);
        BufferedWriter outInitianString = FileHelper.openBufferedWriterStream(PropertyForConversion.PATH_INITIAL_FORM_STRING, PropertyForConversion.ENCODING);
        BufferedWriter outWordString = FileHelper.openBufferedWriterStream(PropertyForConversion.PATH_WORD_FORM_STRING, PropertyForConversion.ENCODING);

        try {
            inDictionary.readLine();
            while (inDictionary.ready()) {
                String[] str = inDictionary.readLine().split("\"");

                String[] lemms = str[0].split(" ");
                outInitianString.append(lemms[0]);
                outInitianString.newLine();

                for (int i = 1; i < str.length; i++) {
                    outWordString.append(str[i].split(" ")[0]);
                    outWordString.newLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outInitianString.close();
                outWordString.close();
            } catch (IOException ex) {
                Logger.getLogger(ConversionDictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void main(String[] args) {
        String old = " <lemma id=\"1\" rev=\"1\"><l t=\"ёж\"><g v=\"NOUN\"/><g v=\"anim\"/><g v=\"masc\"/></l><f t=\"ёж\"><g v=\"sing\"/><g v=\"nomn\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"gent\"/></f><f t=\"ежу\"><g v=\"sing\"/><g v=\"datv\"/></f><f t=\"ежа\"><g v=\"sing\"/><g v=\"accs\"/></f><f t=\"ежом\"><g v=\"sing\"/><g v=\"ablt\"/></f><f t=\"еже\"><g v=\"sing\"/><g v=\"loct\"/></f><f t=\"ежи\"><g v=\"plur\"/><g v=\"nomn\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"gent\"/></f><f t=\"ежам\"><g v=\"plur\"/><g v=\"datv\"/></f><f t=\"ежей\"><g v=\"plur\"/><g v=\"accs\"/></f><f t=\"ежами\"><g v=\"plur\"/><g v=\"ablt\"/></f><f t=\"ежах\"><g v=\"plur\"/><g v=\"loct\"/></f></lemma>";
        System.out.print(conversionLemma(old));
    }

}
