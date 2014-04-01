/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2012 Dave Brosius
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.mebigfatguy.patchanim.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.mebigfatguy.patchanim.BlendDirection;
import com.mebigfatguy.patchanim.OutOfBoundsColor;
import com.mebigfatguy.patchanim.PatchAnimDocument;
import com.mebigfatguy.patchanim.PatchColor;
import com.mebigfatguy.patchanim.ShiftDirection;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedEvent;
import com.mebigfatguy.patchanim.gui.events.ActivePatchChangedListener;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedEvent;
import com.mebigfatguy.patchanim.gui.events.DocumentChangedListener;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedEvent;
import com.mebigfatguy.patchanim.gui.events.SettingsChangedListener;
import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.CombinedPatch;
import com.mebigfatguy.patchanim.surface.Coordinate;
import com.mebigfatguy.patchanim.surface.PatchCoords;
import com.mebigfatguy.patchanim.surface.PatchGenerator;

public class JPatchSamplePanel extends JPanel {
	private static final long serialVersionUID = 8057501623261814175L;
	
	private static final int SAMPLE_SIZE = 200;
	private final PatchColor color;
	private Color rgb;
	private OutOfBoundsColor oob;
	private transient BufferedImage image;
	private PatchDecorator decorator;
	private JPopupMenu contextMenu;
	private JMenu copyPatchMenu;
	private transient Thread redrawThread = null;
	private final Object redrawLock = new Object();
	private boolean redrawing = false;
	@SuppressWarnings("unused") private boolean dragging = false;
	
	public JPatchSamplePanel(PatchColor c) {
		color = c;
		if (c == PatchColor.Red)
			rgb = Color.red;
		else if (c == PatchColor.Green)
			rgb = Color.green;
		else if (c == PatchColor.Blue)
			rgb = Color.blue;
		else if (c == PatchColor.Alpha)
			rgb = Color.white;
		else
			rgb = null;

		initComponents();
		initListeners();
		decorator = null;
	}
	
	public void setDecorator(PatchDecorator patchDecorator) {
		decorator = patchDecorator;
		setEnabled(true);
	}
	
	public void useAlpha(boolean useAlpha) {
	    copyPatchMenu.getMenuComponent(copyPatchMenu.getMenuComponentCount() - 1).setEnabled(useAlpha);
	}
	
	private void initComponents() {
		image = PatchGenerator.buildImage(rgb, false, SAMPLE_SIZE, SAMPLE_SIZE);
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		Dimension d = new Dimension(SAMPLE_SIZE, SAMPLE_SIZE);
		setMinimumSize(d);
		setMaximumSize(d);
		setPreferredSize(d);
		buildPatchContentMenu();
	}
	
	private void initListeners() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if ((color != PatchColor.Combined) && me.isPopupTrigger())
					showPatchContentMenu(me);
 				else if (decorator != null) {
 					dragging = decorator.press(me.getPoint(), JPatchSamplePanel.this.getBounds());
 				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me) {
				if ((color != PatchColor.Combined) && me.isPopupTrigger())
					showPatchContentMenu(me);
 				dragging = false;
			}
		});
		
//		addMouseMotionListener(new MouseMotionAdapter() {
//			@Override
//			public void mouseDragged(MouseEvent me) {
//				if ((decorator != null) && dragging)
//					if (decorator.drag(me.getPoint(), JPatchSamplePanel.this.getBounds())) {
//						PatchPanelMediator mediator = PatchPanelMediator.getMediator();
//						CombinedPatch currentPatch = mediator.getActivePatch();
//						recalcImage(color, currentPatch);
//					}
//			}
//		});
		
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		mediator.addActivePatchChangedListener(new ActivePatchChangedListener() {
			public void activePatchChanged(ActivePatchChangedEvent apce) {
				if (color == PatchColor.Combined) {
					CombinedPatch currentPatch = apce.getActivePatch();
					recalcImage(color, currentPatch);	
				}
			}
		});
		mediator.addDocumentChangedListener(new DocumentChangedListener() {
			public void documentChanged(DocumentChangedEvent dce) {
				PatchAnimDocument doc = dce.getDocument();
				image = PatchGenerator.buildImage(rgb, doc.useAlpha(), SAMPLE_SIZE, SAMPLE_SIZE);
				oob = doc.getOutOfBoundsColor();
				PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
				if ((color != PatchColor.Alpha) || ppMediator.getDocument().useAlpha())
					recalcImage(color, ppMediator.getActivePatch());
			}
		});
		mediator.addSettingsChangedListener(new SettingsChangedListener() {
			public void settingsChanged(SettingsChangedEvent sce) {
				oob = sce.getDocument().getOutOfBoundsColor();
				PatchPanelMediator ppMediator = PatchPanelMediator.getMediator();
				if ((color != PatchColor.Alpha) || ppMediator.getDocument().useAlpha())
					recalcImage(color, ppMediator.getActivePatch());
			}
		});
	}
	
	public void recalcImage(final PatchColor patchColor, final CombinedPatch patch) {
		synchronized(redrawLock) {
			if (redrawing) {
				try {
					redrawThread.interrupt();
					redrawThread.join();
				} catch (InterruptedException ie) {
				} finally {
					redrawThread = null;
				}
			}
		}
		
		redrawThread = new Thread(new Runnable() {
			public void run() {
				if (oob == null)
					oob = OutOfBoundsColor.Clip;
				
				if (patchColor == PatchColor.Combined) {
					PatchGenerator.recalcCombinedImage(patch, image, oob);
				} else {
					PatchGenerator.recalcIndexedImage(patchColor, patch, image, oob);
				}
				
				redraw();
				synchronized(redrawLock) {
					redrawing = false;
				}
			}
		});
		redrawThread.start();
	}
	
	private void redraw() {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					invalidate();
					revalidate();
					repaint();
				}
			});
		} catch (Exception ie) {
			//OK
		}
	}
	
	private void showPatchContentMenu(MouseEvent me) {
	    contextMenu.show(JPatchSamplePanel.this, me.getX(), me.getY());
	}
	
	private void buildPatchContentMenu() {
		final ResourceBundle rb = PatchAnimBundle.getBundle();
		contextMenu = new JPopupMenu();
		JMenu setAllItem = new JMenu(rb.getString(PatchAnimBundle.SETALLPOINTS));
		contextMenu.add(setAllItem);
		{
			JMenuItem blackItem = new JMenuItem(rb.getString(PatchAnimBundle.BLACK));
			blackItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setAllPts(0.0);
				}
			});
			setAllItem.add(blackItem);
			
			JMenuItem fullColorItem = new JMenuItem(rb.getString(PatchAnimBundle.FULLCOLOR));
			fullColorItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setAllPts(255.0);
				}
			});
			setAllItem.add(fullColorItem);
			
			JMenuItem valueItem = new JMenuItem(rb.getString(PatchAnimBundle.VALUE));
			valueItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String value = JOptionPane.showInputDialog(JPatchSamplePanel.this, rb.getString(PatchAnimBundle.VALUE), "128");
					try {
						if (value != null)
							setAllPts(Double.parseDouble(value));
					} catch (NumberFormatException nfe) {
					}
				}
			});
			setAllItem.add(valueItem);	
		}
		
		JMenu borderItem = new JMenu(rb.getString(PatchAnimBundle.SETBORDERPOINTS));
		contextMenu.add(borderItem);
		{
			JMenuItem blackItem = new JMenuItem(rb.getString(PatchAnimBundle.BLACK));
			blackItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setAllBorderPts(0.0);
				}
			});
			borderItem.add(blackItem);
			
			JMenuItem fullColorItem = new JMenuItem(rb.getString(PatchAnimBundle.FULLCOLOR));
			fullColorItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					setAllBorderPts(255.0);
				}
			});
			borderItem.add(fullColorItem);
			
			JMenuItem valueItem = new JMenuItem(rb.getString(PatchAnimBundle.VALUE));
			valueItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					String value = JOptionPane.showInputDialog(JPatchSamplePanel.this, rb.getString(PatchAnimBundle.VALUE), "128");
					try {
						if (value != null)
							setAllBorderPts(Double.parseDouble(value));
					} catch (NumberFormatException nfe) {
					}
				}
			});
			borderItem.add(valueItem);	
		}
		
		JMenuItem setRowItem = new JMenu(rb.getString(PatchAnimBundle.SETROWPOINTS));
		contextMenu.add(setRowItem);
        {
            JMenuItem blackItem = new JMenuItem(rb.getString(PatchAnimBundle.BLACK));
            blackItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setRowPts(0.0);
                }
            });
            setRowItem.add(blackItem);
            
            JMenuItem fullColorItem = new JMenuItem(rb.getString(PatchAnimBundle.FULLCOLOR));
            fullColorItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setRowPts(255.0);
                }
            });
            setRowItem.add(fullColorItem);
            
            JMenuItem valueItem = new JMenuItem(rb.getString(PatchAnimBundle.VALUE));
            valueItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String value = JOptionPane.showInputDialog(JPatchSamplePanel.this, rb.getString(PatchAnimBundle.VALUE), "128");
                    try {
                        if (value != null)
                            setRowPts(Double.parseDouble(value));
                    } catch (NumberFormatException nfe) {
                    }
                }
            });
            setRowItem.add(valueItem);  
        }
		
	    JMenuItem setColumnItem = new JMenu(rb.getString(PatchAnimBundle.SETCOLUMNPOINTS));
	    contextMenu.add(setColumnItem);
        {
            JMenuItem blackItem = new JMenuItem(rb.getString(PatchAnimBundle.BLACK));
            blackItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setColumnPts(0.0);
                }
            });
            setColumnItem.add(blackItem);
            
            JMenuItem fullColorItem = new JMenuItem(rb.getString(PatchAnimBundle.FULLCOLOR));
            fullColorItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setColumnPts(255.0);
                }
            });
            setColumnItem.add(fullColorItem);
            
            JMenuItem valueItem = new JMenuItem(rb.getString(PatchAnimBundle.VALUE));
            valueItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    String value = JOptionPane.showInputDialog(JPatchSamplePanel.this, rb.getString(PatchAnimBundle.VALUE), "128");
                    try {
                        if (value != null)
                            setColumnPts(Double.parseDouble(value));
                    } catch (NumberFormatException nfe) {
                    }
                }
            });
            setColumnItem.add(valueItem);  
        }
	      
		JMenuItem lightenPatch = new JMenuItem(rb.getString(PatchAnimBundle.LIGHTENPATCH));
		lightenPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				deltaAllPts(10.0);
			}
		});		
		contextMenu.add(lightenPatch);

		JMenuItem darkenPatch = new JMenuItem(rb.getString(PatchAnimBundle.DARKENPATCH));
		darkenPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				deltaAllPts(-10.0);
			}
		});
		contextMenu.add(darkenPatch);
		
		JMenu linearGradient = new JMenu(rb.getString(PatchAnimBundle.LINEARGRADIENT));
		JMenuItem leftToRight = new JMenuItem(rb.getString(PatchAnimBundle.LEFTTORIGHT));
		leftToRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				linearGradient(BlendDirection.LeftToRight);
			}
		});
		
		JMenuItem topToBottom = new JMenuItem(rb.getString(PatchAnimBundle.TOPTOBOTTOM));
		topToBottom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				linearGradient(BlendDirection.TopToBottom);
			}
		});
		
		JMenuItem rightToLeft = new JMenuItem(rb.getString(PatchAnimBundle.RIGHTTOLEFT));
		rightToLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				linearGradient(BlendDirection.RightToLeft);
			}
		});

		JMenuItem bottomToTop = new JMenuItem(rb.getString(PatchAnimBundle.BOTTOMTOTOP));
		bottomToTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				linearGradient(BlendDirection.BottomToTop);
			}
		});

		linearGradient.add(leftToRight);
		linearGradient.add(topToBottom);
		linearGradient.add(rightToLeft);
		linearGradient.add(bottomToTop);
		contextMenu.add(linearGradient);
		
		JMenu radialGradient = new JMenu(rb.getString(PatchAnimBundle.RADIALGRADIENT));
		JMenuItem outward = new JMenuItem(rb.getString(PatchAnimBundle.OUTWARD));
		outward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				radialGradient(BlendDirection.Outward);
			}
		});
		
		JMenuItem inward = new JMenuItem(rb.getString(PatchAnimBundle.INWARD));
		inward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				radialGradient(BlendDirection.Inward);
			}
		});
		
		radialGradient.add(outward);
		radialGradient.add(inward);
		contextMenu.add(radialGradient);
		
		JMenu shapeGradient = new JMenu(rb.getString(PatchAnimBundle.SHAPEGRADIENT));
		JMenuItem outwardSh = new JMenuItem(rb.getString(PatchAnimBundle.OUTWARD));
		outwardSh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shapeGradient(BlendDirection.Outward);
			}
		});
		
		JMenuItem inwardSh = new JMenuItem(rb.getString(PatchAnimBundle.INWARD));
		inwardSh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shapeGradient(BlendDirection.Inward);
			}
		});
		
		shapeGradient.add(outwardSh);
		shapeGradient.add(inwardSh);
		contextMenu.add(shapeGradient);
		
		JMenu shift = new JMenu(rb.getString(PatchAnimBundle.SHIFT));
		JMenuItem left = new JMenuItem(rb.getString(PatchAnimBundle.LEFT));
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shiftPatch(ShiftDirection.Left);
			}
		});

		JMenuItem down = new JMenuItem(rb.getString(PatchAnimBundle.DOWN));
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shiftPatch(ShiftDirection.Down);
			}
		});

		JMenuItem right = new JMenuItem(rb.getString(PatchAnimBundle.RIGHT));
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shiftPatch(ShiftDirection.Right);
			}
		});

		JMenuItem up = new JMenuItem(rb.getString(PatchAnimBundle.UP));
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				shiftPatch(ShiftDirection.Up);
			}
		});
		
		shift.add(left);
		shift.add(down);
		shift.add(right);
		shift.add(up);
		contextMenu.add(shift);

		JMenuItem invert = new JMenuItem(rb.getString(PatchAnimBundle.INVERT));
		invert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				invertPatch();
			}
		});
		contextMenu.add(invert);
		
		copyPatchMenu = new JMenu(rb.getString(PatchAnimBundle.COPYPATCHFROM));
		if (color != PatchColor.Red) {
			JMenuItem copyRed = new JMenuItem(rb.getString(PatchAnimBundle.REDPATCH));
			copyRed.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					copyPatch(PatchColor.Red);
				}
			});
			copyPatchMenu.add(copyRed);
		}
		if (color != PatchColor.Green) {
			JMenuItem copyGreen = new JMenuItem(rb.getString(PatchAnimBundle.GREENPATCH));
			copyGreen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					copyPatch(PatchColor.Green);
				}
			});
			copyPatchMenu.add(copyGreen);
		}
		if (color != PatchColor.Blue) {
			JMenuItem copyBlue = new JMenuItem(rb.getString(PatchAnimBundle.BLUEPATCH));
			copyBlue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					copyPatch(PatchColor.Blue);
				}
			});
			copyPatchMenu.add(copyBlue);
		}
		if (color != PatchColor.Alpha) {
          JMenuItem copyAlpha = new JMenuItem(rb.getString(PatchAnimBundle.ALPHAPATCH));
            copyAlpha.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    copyPatch(PatchColor.Alpha);
                }
            });
            copyPatchMenu.add(copyAlpha);
		}
		
		contextMenu.add(copyPatchMenu);
	}
	
	private void invertPatch() {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				c.setColor(255 - c.getColor());
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void copyPatch(PatchColor copyColor) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords srcCoords = patch.getPatch(copyColor);
		PatchCoords dstCoords = patch.getPatch(color);
		int order = srcCoords.getOrder();
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate srcCoord = srcCoords.getCoordinate(i, j);
				double value = srcCoord.getColor();
				Coordinate dstCoord = dstCoords.getCoordinate(i, j);
				dstCoord.setColor(value);
			}
		}
		mediator.setNewActivePatch(patch);
		
	}
	
	private void deltaAllPts(double d) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				c.setColor(c.getColor() + d);
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void setAllPts(double d) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				c.setColor(d);
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void setAllBorderPts(double d) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		for (int k = 0; k < order; k++) {
			Coordinate c = coords.getCoordinate(k, 0);
			c.setColor(d);
			coords.setCoordinate(k, 0, c);
			c = coords.getCoordinate(0, k);
			c.setColor(d);
			coords.setCoordinate(0, k, c);
			c = coords.getCoordinate(k, order-1);
			c.setColor(d);
			coords.setCoordinate(k, order-1, c);
			c = coords.getCoordinate(order-1, k);
			c.setColor(d);
			coords.setCoordinate(order-1, k, c);
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void setRowPts(double d) {
	    PatchPanelMediator mediator = PatchPanelMediator.getMediator();
        CombinedPatch patch = mediator.getActivePatch();
        PatchCoords coords = patch.getPatch(color);
        int order = coords.getOrder();
        int row = decorator.getSelectedCoordinate().y;
        for (int k = 0; k < order; k++) {
            Coordinate c = coords.getCoordinate(k, row);
            c.setColor(d);
            coords.setCoordinate(k, row, c);
        }
        mediator.setNewActivePatch(patch);
	}
	
	private void setColumnPts(double d) {
        PatchPanelMediator mediator = PatchPanelMediator.getMediator();
        CombinedPatch patch = mediator.getActivePatch();
        PatchCoords coords = patch.getPatch(color);
        int order = coords.getOrder();
        int col = decorator.getSelectedCoordinate().x;
        for (int k = 0; k < order; k++) {
            Coordinate c = coords.getCoordinate(col, k);
            c.setColor(d);
            coords.setCoordinate(col, k, c);
        }
        mediator.setNewActivePatch(patch);
	}
	
	private void linearGradient(BlendDirection direction) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		double clr = 0.0;

		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				switch (direction) {
					case LeftToRight:
						clr = (255.0 * i) / (order - 1);
					break;
						
					case TopToBottom:
						clr = (255.0 * j) / (order - 1);
					break;
					
					case RightToLeft:
						clr = (255.0 * (order - 1 - i)) / (order - 1);
					break;
					
					case BottomToTop:
						clr = (255.0 * (order - 1 - j)) / (order - 1);
					break;
				}
				
				c.setColor(clr);
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void radialGradient(BlendDirection direction) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		double clr = 0.0;

		double midOrder = (order - 1) / 2.0;
		double midOrderSq = midOrder * midOrder;
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				switch (direction) {
					case Outward:
						clr = (int)((255.0 * Math.abs(i - midOrder) * Math.abs(j - midOrder)) / midOrderSq);
					break;
						
					case Inward:
						clr = (int)(255.0 - (255.0 * (Math.abs(i - midOrder) * Math.abs(j - midOrder))) / midOrderSq);
					break;
				}
				
				c.setColor(clr);
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	private void shapeGradient(BlendDirection direction) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		double clr = 0.0;

		double midOrder = (order - 1) / 2.0;
		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				double outside = Math.max(Math.abs(i - midOrder), Math.abs(j - midOrder));
				switch (direction) {
					case Outward:
						clr = (int)(255.0 * (outside / midOrder));
					break;
						
					case Inward:
						clr = (int)(255.0 - (255.0 * (outside / midOrder)));
					break;
				}
				
				c.setColor(clr);
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}

	private void shiftPatch(ShiftDirection dir) {
		PatchPanelMediator mediator = PatchPanelMediator.getMediator();
		CombinedPatch patch = mediator.getActivePatch();
		PatchCoords coords = patch.getPatch(color);
		int order = coords.getOrder();
		
		PatchCoords srcCoords = (PatchCoords)coords.clone();

		for (int i = 0; i < order; i++) {
			for (int j = 0; j < order; j++) {
				Coordinate c = coords.getCoordinate(i, j);
				Coordinate srcC = null;
				switch (dir) {
					case Left:
						srcC = srcCoords.getCoordinate((i + 1) % order, j);
					break;
						
					case Down:
						srcC = srcCoords.getCoordinate(i, (j - 1 + order) % order);
					break;
					
					case Right:
						srcC = srcCoords.getCoordinate((i - 1 + order) % order, j);
					break;
					
					case Up:
						srcC = srcCoords.getCoordinate(i, (j + 1) % order);
					break;
					
					default:
						return;
				}
				
				c.setColor(srcC.getColor());
				coords.setCoordinate(i, j, c);
			}
		}
		mediator.setNewActivePatch(patch);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Shape clip = g.getClip();
		try {
			Rectangle bounds = getBounds();
			g.setClip(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());
			
			g.drawImage(image, 0, 0, (int)bounds.getWidth(), (int)bounds.getHeight(), 0, 0, SAMPLE_SIZE, SAMPLE_SIZE, null);
			if (decorator != null)
				decorator.drawDecoration(((Graphics2D) g), g.getClipBounds());
		} finally {
			g.setClip(clip);
		}
	}
}
