package Laporan;

import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import uas_pbo_timnas_u20.InventarisKeluar; // Pastikan ini diimpor dengan benar
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import uas_pbo_timnas_u20.koneksi;

public class LaporanInventarisKeluar {
    private Connection conn;

    public static void main(String[] args) {
        new LaporanInventarisKeluar().createPdf("Laporan Pengeluaran Inventaris.pdf");
    }

    public LaporanInventarisKeluar() {
        conn = new koneksi().connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Koneksi ke database gagal. Periksa konfigurasi koneksi.");
        }
    }

    public void createPdf(String dest) {
        Document document = new Document();
        try {
            File file = new File(dest);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Menambahkan font khusus
            String fontPath = "images/BRLNSDB.TTF";
            BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 14, Font.NORMAL, BaseColor.BLACK);
            
            // Menambahkan judul dengan background color
            Paragraph title = new Paragraph("Laporan Inventaris Keluar", titleFont);
            PdfPCell titleCell = new PdfPCell(title);
            titleCell.setBackgroundColor(new BaseColor(213, 206, 163)); // bg color #D5CEA3
            titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            titleCell.setPadding(10);
            titleCell.setColspan(6);
            
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.addCell(titleCell);
            document.add(titleTable);
            
            document.add(new Paragraph(" ")); // Tambahan spasi

            // Mendapatkan data dari tabel
            InventarisKeluar inventarisKeluar = new InventarisKeluar();
            javax.swing.JTable tableinvklr = inventarisKeluar.getTableInvKlr(); // Metode untuk mendapatkan tableinvklr

            // Menambahkan tabel
            PdfPTable table = new PdfPTable(6); // Menentukan jumlah kolom tabel
            table.setWidthPercentage(100);

            // Menambahkan header tabel
            String[] headers = {"ID Inventaris", "ID Inventaris Masuk", "Jumlah", "Tanggal", "Keterangan", "Nama Lokasi"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(header));
                cell.setBackgroundColor(new BaseColor(255, 215, 0)); // bg color gold, accent 4 lighter 80%
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Menambahkan data ke tabel
            for (int i = 0; i < tableinvklr.getRowCount(); i++) {
                table.addCell(tableinvklr.getValueAt(i, 0).toString());
                table.addCell(tableinvklr.getValueAt(i, 1).toString());
                table.addCell(tableinvklr.getValueAt(i, 2).toString());
                table.addCell(tableinvklr.getValueAt(i, 3).toString());
                table.addCell(tableinvklr.getValueAt(i, 4).toString());

                // Mengambil nama lokasi dari query
                String id_inv_msk = tableinvklr.getValueAt(i, 1).toString();
                String nama_lokasi = "";
                try {
                    String sql2 = "SELECT c.nama_lokasi FROM trx_inventaris_masuk a, mst_lokasi c WHERE a.kd_lokasi=c.kd_lokasi AND a.kd_inventaris_masuk='" + id_inv_msk + "'";
                    Statement stat2 = conn.createStatement();
                    ResultSet hasil2 = stat2.executeQuery(sql2);
                    if (hasil2.next()) {
                        nama_lokasi = hasil2.getString("nama_lokasi");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Data gagal dipanggil " + e);
                }
                table.addCell(nama_lokasi);
            }

            document.add(table);
            document.close();
            System.out.println("PDF Created at: " + file.getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat dokumen: " + e.getMessage());
        }
    }
}
