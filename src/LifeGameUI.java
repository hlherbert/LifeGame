import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class LifeGameUI {
    private static final int WAIT_TIME_MS = 100; // 每次迭代后延迟的时间
    private static final int PLAY_TIMES = 1; // 每次PLAY的次数
    private static final int PLAY_STEP = 1; // 每次PLAY，迭代次数
    private static final int CELL_SIZE = 10; // 细胞的大小
    private Grid grid = new Grid();
    private BufferedImage img;
    private int gridSize = 0;
    private int cellNum = 0;
    private int liveThreshold = 0;
    private JButton btnPlay;
    private JButton btnReset;
    private JPanel pnlArea;
    private JTextField txtGridSize;
    private JTextField txtCellNum;
    private JTextField txtLiveThreshold;

    /**
     * { 创建并显示GUI。出于线程安全的考虑， 这个方法在事件调用线程中调用。
     */
    private void createAndShowGUI() {
        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("生命游戏");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * 创建面板，这个类似于 HTML 的 div 标签 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置按钮的行为
        setComponentActions();

        // 显示窗口
        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {

        /*
         * 布局部分我们这边不多做介绍 这边设置布局为 null
         */
        panel.setLayout(null);

        /*
         * 这个方法定义了组件的位置。 setBounds(x, y, width, height) x 和 y 指定左上角的新位置，由 width
         * 和 height 指定新的大小。
         */
        // 输入
        JLabel lblSize = new JLabel("空间大小:");
        lblSize.setBounds(10, 20, 80, 25);
        panel.add(lblSize);

        /*
         * 创建文本域用于用户输入
         */
        JTextField txtSize = new JTextField(20);
        txtSize.setBounds(100, 20, 165, 25);
        txtSize.setText("80");
        panel.add(txtSize);
        this.txtGridSize = txtSize;

        // 输入
        JLabel lblCellNum = new JLabel("细胞个数:");
        lblCellNum.setBounds(10, 50, 80, 25);
        panel.add(lblCellNum);

        /*
         * 创建文本域用于用户输入
         */
        JTextField txtCellNum = new JTextField(20);
        txtCellNum.setBounds(100, 50, 165, 25);
        txtCellNum.setText("1000");
        panel.add(txtCellNum);
        this.txtCellNum = txtCellNum;

        // 输入
        JLabel lblLiveThreshold = new JLabel("存活阈值:");
        lblLiveThreshold.setBounds(10, 80, 80, 25);
        panel.add(lblLiveThreshold);

        /*
         * 创建文本域用于用户输入
         */
        JTextField txtLiveThreshold = new JTextField(20);
        txtLiveThreshold.setBounds(100, 80, 165, 25);
        txtLiveThreshold.setText("3");
        panel.add(txtLiveThreshold);
        this.txtLiveThreshold = txtLiveThreshold;

        // 创建PLAY按钮
        JButton btnPlay = new JButton("Play");
        btnPlay.setBounds(10, 110, 80, 25);
        panel.add(btnPlay);
        this.btnPlay = btnPlay;

        // 创建重置按钮
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(100, 110, 80, 25);
        panel.add(btnReset);
        this.btnReset = btnReset;

        JPanel pnlArea = new JPanel() {
            @Override
            public void paint(Graphics g) {
                drawGrid(g, grid);
            }
        };
        pnlArea.setBounds(10, 150, 500, 500);
        // pnlArea.setBackground(Color.white);
        panel.add(pnlArea);
        this.pnlArea = pnlArea;
    }

    // 读取输入的数据
    private void loadInputParams() {
        String strGridSize = this.txtGridSize.getText();
        this.gridSize = Integer.parseInt(strGridSize);

        String strCellNum = this.txtCellNum.getText();
        this.cellNum = Integer.parseInt(strCellNum);

        String strLiveTherhold = this.txtLiveThreshold.getText();
        this.liveThreshold = Integer.parseInt(strLiveTherhold);
    }

    private void setComponentActions() {

        // play 按钮按下
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (grid.isEmpty()) {
                    loadInputParams();
                    grid.create(gridSize, gridSize, cellNum, liveThreshold);
                    grid.reset();
                }
                for (int i = 0; i < PLAY_TIMES; i++) {
                    grid.nextIter(PLAY_STEP);
                    // drawGrid(pnlArea.getGraphics(), grid);
                    try {
                        Thread.sleep(WAIT_TIME_MS);
                        drawGrid(pnlArea.getGraphics(), grid);
                        // pnlArea.repaint();
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

        // reset 按钮按下
        btnReset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                loadInputParams();
                grid.create(gridSize, gridSize, cellNum, liveThreshold);
                grid.reset();
                img = ImageUtil.createImageFromCells(grid.getCells(),
                        grid.getWidth(), grid.getHeight());
                pnlArea.setBounds(pnlArea.getX(), pnlArea.getY(), gridSize
                        * CELL_SIZE, gridSize * CELL_SIZE);
                // drawGrid(pnlArea.getGraphics(), grid);
            }
        });
    }

    private void drawGrid(Graphics g, Grid grid) {
        if (grid == null) {
            return;
        }
        if (grid.isEmpty()) {
            return;
        }

        int c = CELL_SIZE;// 细胞大小，像素
        int w = grid.getWidth();
        int h = grid.getHeight();

        if (w <= 0 || h <= 0) {
            return;
        }

        // 空白面板
        g.setColor(Color.white);
        g.fillRect(0, 0, w * c, h * c);

        // 活细胞的填充色
        g.setColor(Color.black);
        int[][] cells = grid.getCells();
        if (cells == null) {
            return;
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int cell = cells[i][j];

                // 活细胞的填充色
                g.setColor(new Color(cell));

                // 细胞活着，就在对应的格子填上颜色
                if (Grid.isLive(cell)) {
                    g.fillRect(i * c, j * c, c, c);
                }
            }
        }
    }

    public static void main(String[] args) {
        // 显示应用 GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LifeGameUI ui = new LifeGameUI();
                ui.createAndShowGUI();
            }
        });
    }
}