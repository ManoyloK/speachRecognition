package main.java.org.manoilok.speachrecognition.model;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Modest on 07.09.2016.
 */
public class OpenFileDialog extends JFrame {

    private JFileChooser dialog = new JFileChooser();

    public OpenFileDialog() throws HeadlessException {
        setBounds(0, 0, 500, 500);

        dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //dialog.setCurrentDirectory(new File(System.getProperty("D://Study/5/Распознавание/SOUND")));
        dialog.setApproveButtonText("Выбрать");//выбрать название для кнопки согласия
        dialog.setDialogTitle("Выберите файл для загрузки");// выбрать название
        dialog.setDialogType(JFileChooser.OPEN_DIALOG);// выбрать тип диалога Open или Save
        dialog.setMultiSelectionEnabled(true); // Разрегить выбор нескольки файлов
        dialog.showOpenDialog(this);

    }

    public File getFile(){
        File[] file = dialog.getSelectedFiles();
        //setVisible(true);
        if (file.length>0)
            return file[0];
        else
            return null;
    }
    public File[] getAllFiles(){
        File[] files = dialog.getSelectedFiles();
        //setVisible(true);
        if (files.length>0)
            return files;
        else
            return null;
    }
}
