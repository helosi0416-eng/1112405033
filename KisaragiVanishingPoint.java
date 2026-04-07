import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;

public class KisaragiVanishingPoint extends JPanel {
    private Image image;
    private ArrayList<Point> points = new ArrayList<>();
    private Point vanishingPoint = null;

    public KisaragiVanishingPoint() {
        // 讀取如月車站的照片 (請確認你的檔名是 kisaragi.jpg)
        ImageIcon icon = new ImageIcon("kisaragi.jpg");
        
        // 將照片強制縮放到 800x600 方便在視窗中點擊
        image = icon.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 如果點擊次數少於 4 次，就繼續記錄
                if (points.size() < 4) {
                    points.add(e.getPoint());
                    
                    // 當收集到 4 個點時，算交點！
                    if (points.size() == 4) {
                        calculateVanishingPoint();
                    }
                    repaint(); 
                } else {
                    // 點第 5 下時，清空畫面讓你重新測量
                    points.clear();
                    vanishingPoint = null;
                    repaint();
                }
            }
        });
    }

    // 計算兩條直線交點的數學邏輯
    private void calculateVanishingPoint() {
        Point p1 = points.get(0);
        Point p2 = points.get(1);
        Point p3 = points.get(2);
        Point p4 = points.get(3);

        double a1 = p2.y - p1.y;
        double b1 = p1.x - p2.x;
        double c1 = a1 * p1.x + b1 * p1.y;

        double a2 = p4.y - p3.y;
        double b2 = p3.x - p4.x;
        double c2 = a2 * p3.x + b2 * p3.y;

        double det = a1 * b2 - a2 * b1;

        if (det != 0) {
            double x = (b2 * c1 - b1 * c2) / det;
            double y = (a1 * c2 - a2 * c1) / det;
            vanishingPoint = new Point((int)x, (int)y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(3)); 

        // 畫出你點擊的點 (紅點)
        g2d.setColor(Color.RED);
        for (Point p : points) {
            g2d.fillOval(p.x - 5, p.y - 5, 10, 10); 
        }

        // 畫出第一條線 (綠線)
        if (points.size() >= 2) {
            g2d.setColor(Color.GREEN);
            g2d.drawLine(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
        }

        // 畫出第二條線 (藍線)
        if (points.size() >= 4) {
            g2d.setColor(Color.BLUE);
            g2d.drawLine(points.get(2).x, points.get(2).y, points.get(3).x, points.get(3).y);
        }

        // 如果成功算出消失點，把它畫出來
        if (vanishingPoint != null) {
            // 紫色大圓點 (消失點)
            g2d.setColor(Color.MAGENTA);
            g2d.fillOval(vanishingPoint.x - 8, vanishingPoint.y - 8, 16, 16);
            
            // 畫出往消失點的延伸虛線
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
            g2d.drawLine(points.get(1).x, points.get(1).y, vanishingPoint.x, vanishingPoint.y);
            g2d.drawLine(points.get(3).x, points.get(3).y, vanishingPoint.x, vanishingPoint.y);

            // 畫出黃色地平線 (Vanish Line)
            g2d.setColor(Color.YELLOW);
            g2d.drawLine(0, vanishingPoint.y, getWidth(), vanishingPoint.y);
        }

        // 左上角的文字提示框
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(10, 10, 600, 40);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("微軟正黑體", Font.BOLD, 18));
        
        String text = "請點擊鐵軌邊緣找消失點 (目前 " + points.size() + "/4 點)";
        if (vanishingPoint != null) {
            text = "🎯 找到消失點！ X: " + vanishingPoint.x + ", Y: " + vanishingPoint.y + " (點擊畫面重置)";
        }
        g2d.drawString(text, 20, 36);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("如月車站 - 消失點尋找工具");
        KisaragiVanishingPoint panel = new KisaragiVanishingPoint();
        frame.add(panel);
        frame.setSize(800, 630); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 讓視窗顯示在螢幕正中間
        frame.setVisible(true);
    }
}