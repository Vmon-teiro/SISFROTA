package service;

import controller.RelatorioController.CustoEmbarcacaoDTO;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class RelatorioPDFService {

    public static File gerarPDF(List<CustoEmbarcacaoDTO> dados, double totalGeral) throws Exception {
        // Mapeia o caminho padrão da pasta Downloads do usuário no Windows, Linux ou Mac
        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");

        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }

        File pdfFile = new File(downloadsDir, "Relatorio_Custos_Manutencao.pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

        document.open();

        Font fontTitulo = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Relatório Consolidado de Custos de Manutenção", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell("Embarcação");
        table.addCell("Manutenções Concluídas");
        table.addCell("Custo Acumulado (R$)");

        for (CustoEmbarcacaoDTO item : dados) {
            table.addCell(item.getNomeEmbarcacao());
            table.addCell(String.valueOf(item.getTotalManutencoes()));
            table.addCell(String.format("R$ %.2f", item.getCustoTotal()));
        }

        document.add(table);
        document.add(new Paragraph(" "));
        
        Font fontTotal = new Font(Font.HELVETICA, 12, Font.BOLD);
        document.add(new Paragraph(String.format("Custo Total Geral: R$ %.2f", totalGeral), fontTotal));

        document.close();
        return pdfFile;
    }
}