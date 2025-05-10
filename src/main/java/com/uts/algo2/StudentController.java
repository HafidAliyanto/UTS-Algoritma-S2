package com.uts.algo2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML
    private TableView<Student> tabelSiswa;
    @FXML
    private TableColumn<Student, String> kolomNim;
    @FXML
    private TableColumn<Student, String> kolomNama;
    @FXML
    private TableColumn<Student, Double> kolomNilai;
    @FXML
    private TextField fieldNim;
    @FXML
    private TextField fieldNama;
    @FXML
    private TextField fieldNilai;
    @FXML
    private TextField fieldPencarian;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelRataRata;
    @FXML
    private Label labelHasilPencarian;

    private ObservableList<Student> daftarSiswa = FXCollections.observableArrayList();
    private final String FILE_PATH = "siswa.csv";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inisialisasi kolom tabel
        kolomNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        kolomNilai.setCellValueFactory(new PropertyValueFactory<>("nilai"));

        // Muat data dari file
        muatDataSiswa();

        // Hitung statistik
        updateStatistik();
    }

    private void muatDataSiswa() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            daftarSiswa.clear();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String nim = parts[0];
                    String nama = parts[1];
                    double nilai = Double.parseDouble(parts[2]);
                    daftarSiswa.add(new Student(nim, nama, nilai));
                }
            }
            reader.close();
            tabelSiswa.setItems(daftarSiswa);
        } catch (IOException e) {
            tampilkanAlert("Error memuat data: " + e.getMessage());
        }
    }

    @FXML
    private void tambahSiswa() {
        try {
            String nim = fieldNim.getText().trim();
            String nama = fieldNama.getText().trim();
            double nilai = Double.parseDouble(fieldNilai.getText().trim());

            if (nim.isEmpty() || nama.isEmpty()) {
                tampilkanAlert("NIM dan Nama tidak boleh kosong");
                return;
            }

            Student siswa = new Student(nim, nama, nilai);
            daftarSiswa.add(siswa);

            // Simpan ke file
            simpanDataSiswa();

            // Bersihkan field input
            fieldNim.clear();
            fieldNama.clear();
            fieldNilai.clear();

            // Update statistik
            updateStatistik();
        } catch (NumberFormatException e) {
            tampilkanAlert("Format nilai tidak valid. Silakan masukkan angka yang valid.");
        }
    }

    private void simpanDataSiswa() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
            for (Student siswa : daftarSiswa) {
                writer.write(siswa.toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            tampilkanAlert("Error menyimpan data: " + e.getMessage());
        }
    }

    @FXML
    private void urutkanBerdasarkanNilai() {
        Collections.sort(daftarSiswa);
        tabelSiswa.refresh();
    }

    @FXML
    private void cariSiswa() {
        String namaPencarian = fieldPencarian.getText().trim().toLowerCase();
        if (namaPencarian.isEmpty()) {
            labelHasilPencarian.setText("Silakan masukkan nama untuk mencari");
            return;
        }

        // Urutkan berdasarkan nama untuk binary search
        List<Student> urutBerdasarkanNama = new ArrayList<>(daftarSiswa);
        urutBerdasarkanNama.sort((s1, s2) -> s1.getNama().toLowerCase().compareTo(s2.getNama().toLowerCase()));

        int index = pencarianBiner(urutBerdasarkanNama, namaPencarian, 0, urutBerdasarkanNama.size() - 1);

        if (index != -1) {
            Student ditemukan = urutBerdasarkanNama.get(index);
            labelHasilPencarian.setText("Ditemukan: " + ditemukan.getNama() + " dengan nilai " + ditemukan.getNilai());

            // Pilih dan scroll ke siswa yang ditemukan dalam tabel
            for (int i = 0; i < daftarSiswa.size(); i++) {
                if (daftarSiswa.get(i).getNim().equals(ditemukan.getNim())) {
                    tabelSiswa.getSelectionModel().select(i);
                    tabelSiswa.scrollTo(i);
                    break;
                }
            }
        } else {
            labelHasilPencarian.setText("Siswa tidak ditemukan");
        }
    }

    private int pencarianBiner(List<Student> siswa, String nama, int bawah, int atas) {
        if (atas < bawah) {
            return -1;
        }

        int tengah = bawah + (atas - bawah) / 2;
        String namaTengah = siswa.get(tengah).getNama().toLowerCase();

        if (namaTengah.equals(nama)) {
            return tengah;
        } else if (namaTengah.compareTo(nama) > 0) {
            return pencarianBiner(siswa, nama, bawah, tengah - 1);
        } else {
            return pencarianBiner(siswa, nama, tengah + 1, atas);
        }
    }

    private void updateStatistik() {
        double total = hitungTotalNilai(daftarSiswa, 0, 0);
        double rataRata = daftarSiswa.isEmpty() ? 0 : total / daftarSiswa.size();

        labelTotal.setText("Total Nilai: " + String.format("%.2f", total));
        labelRataRata.setText("Rata-rata Nilai: " + String.format("%.2f", rataRata));
    }

    // Metode rekursif untuk menghitung total nilai
    private double hitungTotalNilai(List<Student> siswa, int index, double jumlah) {
        if (index >= siswa.size()) {
            return jumlah;
        }
        return hitungTotalNilai(siswa, index + 1, jumlah + siswa.get(index).getNilai());
    }

    private void tampilkanAlert(String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    @FXML
    private void kembaliKePrimary() throws IOException {
        App.setRoot("primary");
    }
}
