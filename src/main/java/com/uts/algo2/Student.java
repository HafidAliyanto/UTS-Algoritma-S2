package com.uts.algo2;

public class Student implements Comparable<Student> {
    private String nim;
    private String nama;
    private double nilai;

    public Student(String nim, String nama, double nilai) {
        this.nim = nim;
        this.nama = nama;
        this.nilai = nilai;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getNilai() {
        return nilai;
    }

    public void setNilai(double nilai) {
        this.nilai = nilai;
    }

    @Override
    public String toString() {
        return nim + "," + nama + "," + nilai;
    }

    @Override
    public int compareTo(Student other) {
        // Untuk urutan menurun (descending), balik perbandingan
        return Double.compare(other.nilai, this.nilai);
    }
}
