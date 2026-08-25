package view;

import javax.swing.*;
import java.awt.*;

public class TelaDashboardAdmin extends JFrame {

    public TelaDashboardAdmin() {
        setTitle("Dashboard Financeiro - Consolidação de Custos");
        setSize(850, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new PainelDashboardAdmin(), BorderLayout.CENTER);
    }
}