package org.joverseer.ui.map.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;

/**
 * Renders a hex (terrain, sides, etc)
 * 
 * @author Marios Skounakis
 */
public class DefaultHexRenderer extends ImageRenderer implements ApplicationListener {
	HashMap<Integer, Color> terrainColors = new HashMap<Integer, Color>();

	protected int[] xPoints = new int[6];
	protected int[] yPoints = new int[6];

	Point hexCenter;

	protected Polygon polygon;
	protected MapMetadata metadata = null;

	Color majorRiverColor;
	Color minorRiverColor;
	Color roadColor;
	Color bridgeColor;
	Color fordColor;
	HashMap<MapOptionsEnum, Object> mapOptions;

	public DefaultHexRenderer() {
	}

	@SuppressWarnings("unchecked")
	protected void init() {
		this.metadata = MapMetadata.instance();
		this.xPoints[0] = this.metadata.getHexSize() / 2 * this.metadata.getGridCellWidth();
		this.xPoints[1] = this.metadata.getHexSize() * this.metadata.getGridCellWidth();
		this.xPoints[2] = this.metadata.getHexSize() * this.metadata.getGridCellWidth();
		this.xPoints[3] = this.metadata.getHexSize() / 2 * this.metadata.getGridCellWidth();
		this.xPoints[4] = 0;
		this.xPoints[5] = 0;

		this.yPoints[0] = 0;
		this.yPoints[1] = this.metadata.getHexSize() / 4 * this.metadata.getGridCellHeight();
		this.yPoints[2] = this.metadata.getHexSize() * 3 / 4 * this.metadata.getGridCellHeight();
		this.yPoints[3] = this.metadata.getHexSize() * this.metadata.getGridCellHeight();
		this.yPoints[4] = this.metadata.getHexSize() * 3 / 4 * this.metadata.getGridCellHeight();
		this.yPoints[5] = this.metadata.getHexSize() / 4 * this.metadata.getGridCellHeight();

		this.hexCenter = new Point(this.metadata.getHexSize() / 2 * this.metadata.getGridCellWidth(), this.metadata.getHexSize() / 2 * this.metadata.getGridCellHeight());

		MessageSource colorSource = (MessageSource) Application.instance().getApplicationContext().getBean("colorSource");

		String colorStr = "#DDDDDD";
		for (HexTerrainEnum t : HexTerrainEnum.values()) {
			colorStr = colorSource.getMessage(t.toString() + ".color", null, Locale.getDefault());
			setTerrainColor(t, Color.decode(colorStr));
		}

		colorStr = colorSource.getMessage("majorRiver.color", null, Locale.getDefault());
		setMajorRiverColor(Color.decode(colorStr));
		colorStr = colorSource.getMessage("minorRiver.color", null, Locale.getDefault());
		setMinorRiverColor(Color.decode(colorStr));
		colorStr = colorSource.getMessage("road.color", null, Locale.getDefault());
		setRoadColor(Color.decode(colorStr));
		colorStr = colorSource.getMessage("bridge.color", null, Locale.getDefault());
		setBridgeColor(Color.decode(colorStr));
		colorStr = colorSource.getMessage("ford.color", null, Locale.getDefault());
		setFordColor(Color.decode(colorStr));

		this.images.clear();

		this.mapOptions = (HashMap<MapOptionsEnum, Object>) Application.instance().getApplicationContext().getBean("mapOptions");
	}

	protected Polygon getSidePolygon(HexSideEnum side) {
		int i = side.getSide();
		Polygon p = new Polygon(new int[] { this.xPoints[i - 1 % 6], this.xPoints[i % 6] }, new int[] { this.yPoints[i - 1 % 6], this.yPoints[i % 6] }, 2);
		return p;
	}

	protected Polygon getSidePolygonInner(HexSideEnum side) {
		int i = side.getSide();
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
		if (side == HexSideEnum.BottomLeft) {
			x1 = 0;
			x2 = 2;
			y1 = -2;
			y2 = -2;
		} else if (side == HexSideEnum.Left) {
			x1 = 2;
			x2 = 2;
			y1 = -2;
			y2 = 2;
		} else if (side == HexSideEnum.TopLeft) {
			x1 = 2;
			x2 = 0;
			y1 = 2;
			y2 = 2;
		} else if (side == HexSideEnum.TopRight) {
			x1 = 0;
			x2 = -2;
			y1 = 2;
			y2 = 2;
		} else if (side == HexSideEnum.Right) {
			x1 = -2;
			x2 = -2;
			y1 = 0;
			y2 = 0;
		} else if (side == HexSideEnum.BottomRight) {
			x1 = -2;
			x2 = 0;
			y1 = -2;
			y2 = -2;
		}
		Polygon p = new Polygon(new int[] { this.xPoints[i - 1 % 6] + x1, this.xPoints[i % 6] + x2 }, new int[] { this.yPoints[i - 1 % 6] + y1, this.yPoints[i % 6] + y2 }, 2);
		return p;
	}

	protected Point getSideCenter(HexSideEnum side) {
		int i = side.getSide();
		return new Point((this.xPoints[i % 6] + this.xPoints[i - 1]) / 2, (this.yPoints[i % 6] + this.yPoints[i - 1]) / 2);
	}

	@Override
	public boolean appliesTo(Object obj) {
		return Hex.class.isInstance(obj);
	}

	protected Color getColor(Hex hex) {
		if (this.terrainColors.containsKey(hex.getTerrain().getTerrain())) {
			return this.terrainColors.get(hex.getTerrain().getTerrain());
		}
		return Color.white;
	}

	public void renderRoad(Graphics2D g, HexSideEnum side, int x, int y) {
		Stroke s = g.getStroke();
		Stroke r = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Point sideCenter = getSideCenter(side);
		Point center = new Point(this.hexCenter);
		sideCenter.translate(x, y);
		center.translate(x, y);
		g.setColor(getRoadColor());
		g.setStroke(r);
		g.drawLine(center.x, center.y, sideCenter.x, sideCenter.y);
		g.setStroke(s);

	}

	public void renderMajorRiver(Graphics2D g, HexSideEnum side, int x, int y) {
		Stroke s = g.getStroke();
		Stroke r = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Polygon sp = getSidePolygon(side);
		sp.translate(x, y);
		g.setColor(getMajorRiverColor());

		g.setStroke(r);
		g.drawPolygon(sp);
		g.setStroke(s);
	}

	public void renderMinorRiver(Graphics2D g, HexSideEnum side, int x, int y) {
		Stroke s = g.getStroke();
		Stroke r = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		Polygon sp = getSidePolygon(side);
		sp.translate(x, y);
		g.setColor(getMinorRiverColor());
		g.setStroke(r);
		g.drawPolygon(sp);
		g.setStroke(s);
	}

	public void renderBridge(Graphics2D g, HexSideEnum side, int x, int y) {
		Stroke s = g.getStroke();
		Stroke r = new BasicStroke(6);
		Point sideCenter = getSideCenter(side);
		Point center = new Point(this.hexCenter);
		Point start = new Point((center.x + 2 * sideCenter.x) / 3, (center.y + 2 * sideCenter.y) / 3);
		start.translate(x, y);
		sideCenter.translate(x, y);
		g.setColor(getBridgeColor());
		g.setStroke(r);
		g.drawLine(start.x, start.y, sideCenter.x, sideCenter.y);
		g.setStroke(s);
	}

	public void renderFord(Graphics2D g, HexSideEnum side, int x, int y) {
		Stroke s = g.getStroke();
		Stroke r = new BasicStroke(6);
		Point sideCenter = getSideCenter(side);
		Point center = new Point(this.hexCenter);
		Point start = new Point((center.x + 2 * sideCenter.x) / 3, (center.y + 2 * sideCenter.y) / 3);
		start.translate(x, y);
		sideCenter.translate(x, y);
		g.setColor(getFordColor());
		g.setStroke(r);
		g.drawLine(start.x, start.y, sideCenter.x, sideCenter.y);
		g.setStroke(s);
	}

	protected boolean withinMapRange(int x, int y, MapMetadata metadata1) {
		if (x < metadata1.getMinMapColumn())
			return false;
		if (x > metadata1.getMaxMapColumn())
			return false;
		if (y < metadata1.getMinMapRow())
			return false;
		if (y > metadata1.getMaxMapRow())
			return false;
		return true;
	}

	@Override
	public void render(Object obj, Graphics2D g, int x, int y) {
		if (!appliesTo(obj)) {
			throw new IllegalArgumentException(obj.toString());
		}

		if (this.metadata == null) {
			init();
		}

		Hex hex = (Hex) obj;
		if (!withinMapRange(hex.getColumn(), hex.getRow(), this.metadata))
			return;

		boolean imageDrawn = false;

		String pval = PreferenceRegistry.instance().getPreferenceValue("map.terrainGraphics");
		if (pval.equals("texture")) {
			BufferedImage img = getImage(hex.getTerrain().toString() + ".terrain", this.metadata.getGridCellWidth() * this.metadata.getHexSize(), this.metadata.getGridCellHeight() * this.metadata.getHexSize());
			if (img != null) {
				g.drawImage(img, x, y, null);
				Polygon polygon1 = new Polygon(this.xPoints, this.yPoints, 6);
				polygon1.translate(x, y);
				g.setColor(Color.black);
				g.drawPolygon(polygon1);
				imageDrawn = true;
			}
		}
		if (!imageDrawn) {
			Polygon polygon1 = new Polygon(this.xPoints, this.yPoints, 6);
			polygon1.translate(x, y);
			g.setColor(getColor(hex));
			g.fillPolygon(polygon1);
			g.setColor(Color.black);
			g.drawPolygon(polygon1);
		}
		for (HexSideEnum side : HexSideEnum.values()) {
			Collection<HexSideElementEnum> elements = hex.getHexSideElements(side);
			if (elements.size() > 0) {
				if (elements.contains(HexSideElementEnum.MajorRiver)) {
					renderMajorRiver(g, side, x, y);
				} else if (elements.contains(HexSideElementEnum.MinorRiver)) {
					renderMinorRiver(g, side, x, y);
				}
				;
				if (elements.contains(HexSideElementEnum.Road)) {
					renderRoad(g, side, x, y);
				}
				;
				if (elements.contains(HexSideElementEnum.Bridge)) {
					renderBridge(g, side, x, y);
				}
				;
				if (elements.contains(HexSideElementEnum.Ford)) {
					renderFord(g, side, x, y);
				}
				;

			}
		}
	}

	public void setTerrainColor(HexTerrainEnum terrain, Color c) {
		setTerrainColor(terrain.getTerrain(), c);
	}

	public void setTerrainColor(int i, Color c) {
		if (this.terrainColors.containsKey(i)) {
			this.terrainColors.remove(i);
		}
		this.terrainColors.put(i, c);
	}

	public Color getBridgeColor() {
		return this.bridgeColor;
	}

	public void setBridgeColor(Color bridgeColor) {
		this.bridgeColor = bridgeColor;
	}

	public Color getFordColor() {
		return this.fordColor;
	}

	public void setFordColor(Color fordColor) {
		this.fordColor = fordColor;
	}

	public Color getMajorRiverColor() {
		return this.majorRiverColor;
	}

	public void setMajorRiverColor(Color majorRiverColor) {
		this.majorRiverColor = majorRiverColor;
	}

	public Color getMinorRiverColor() {
		return this.minorRiverColor;
	}

	public void setMinorRiverColor(Color minorRiverColor) {
		this.minorRiverColor = minorRiverColor;
	}

	public Color getRoadColor() {
		return this.roadColor;
	}

	public void setRoadColor(Color roadColor) {
		this.roadColor = roadColor;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.isLifecycleEvent(LifecycleEventsEnum.MapMetadataChangedEvent)) {
				init();
			}
		}
	}

}
