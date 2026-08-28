package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GeradorPDFService {

    public static String gerarRelatorioCustos(List<Object[]> dadosCustos) throws Exception {
        String userHome = System.getProperty("user.home");
        String caminhoDownloads = userHome + File.separator + "Downloads" + File.separator + "Relatorio_Custos_Embarcacoes.pdf";

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, new FileOutputStream(caminhoDownloads));

        document.open();

        // Cabeçalho do Documento
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font fontHeaderTabela = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font fontDados = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph pTitulo = new Paragraph("GESTÃO NÁUTICA - RELATÓRIO DE CUSTOS", fontTitulo);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitulo);

        String dataAtual = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
        Paragraph pSub = new Paragraph("Gerado em: " + dataAtual + " | Módulo Administrador", fontSubtitulo);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(20);
        document.add(pSub);

        // Tabela de Custos
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3f, 2f, 2f, 2f});

        String[] headers = {"Embarcação", "Manutenção (R$)", "Combustível (R$)", "Custo Total (R$)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeaderTabela));
            cell.setBackgroundColor(new Color(41, 128, 185));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
        double geralTotal = 0;

        for (Object[] row : dadosCustos) {
            double maint = (double) row[2];
            double fuel = (double) row[3];
            double total = (double) row[4];

            geralTotal += total;

            table.addCell(new Phrase((String) row[1], fontDados));
            table.addCell(new Phrase(currencyFormat.format(maint), fontDados));
            table.addCell(new Phrase(currencyFormat.format(fuel), fontDados));
            table.addCell(new Phrase(currencyFormat.format(total), fontDados));
        }

        document.add(table);

        // Resumo Geral
        Paragraph pResumo = new Paragraph("\nTOTAL GERAL OPERACIONAL: " + currencyFormat.format(geralTotal), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY));
        pResumo.setAlignment(Element.ALIGN_RIGHT);
        document.add(pResumo);

        document.close();
        return caminhoDownloads;
    }
}