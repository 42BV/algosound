package algoimage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class AlgoImage {

	static ComplexNumber julia = ComplexNumber.of(-0.7, 0.27015);
	
	static Function<Integer, Color> pixel = (r) -> r==0 ? Color.BLUE: Color.WHITE;
	static Function<Integer, Color> rainbow = (r) -> Color.getHSBColor(r/256.0f, 1.0f, 1.0f);
	
	public static void main(String[] args) {
		show(pixel, (x, y) -> (x ^ y) % 1);
	}

	private static void show(Function<Integer, Color> colMap, BiFunction<Integer, Integer, Integer> fun) {
		int w = 255;
		int h = 255;
		int scale = 2;
		JFrame frame = new JFrame();
		JPanel owner = new JPanel() {

			@Override
			public void paint(Graphics g) {
				for (int x = 0; x < getWidth() / scale; x++) {
					for (int y = 0; y < getHeight() / scale; y++) {
						int r = fun.apply(x, y);
						Color col = colMap.apply(r);
						g.setColor(col);
						g.fillRect(x * scale, y * scale, scale, scale);
					}
				}
			}

		};
		owner.setPreferredSize(new Dimension(w * scale, h * scale));

		JPanel outer = new JPanel();
		outer.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
		outer.setBackground(Color.WHITE);
		outer.add(owner, BorderLayout.CENTER);

		frame.setContentPane(outer);
		frame.setSize(w * scale + 64, h * scale + 108);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}