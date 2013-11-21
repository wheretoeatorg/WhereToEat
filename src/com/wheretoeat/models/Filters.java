package com.wheretoeat.models;

public class Filters {

	private boolean showVisited;
	private boolean openNow;
	private boolean price1;
	private boolean price2;
	private boolean price3;
	private boolean price4;

	public boolean isShowVisited() {
		return showVisited;
	}

	public void setShowVisited(boolean showVisited) {
		this.showVisited = showVisited;
	}

	public boolean isOpenNow() {
		return openNow;
	}

	public void setOpenNow(boolean openNow) {
		this.openNow = openNow;
	}

	public boolean isPrice1() {
		return price1;
	}

	public void setPrice1(boolean price1) {
		this.price1 = price1;
	}

	public boolean isPrice2() {
		return price2;
	}

	public void setPrice2(boolean price2) {
		this.price2 = price2;
	}

	public boolean isPrice3() {
		return price3;
	}

	public void setPrice3(boolean price3) {
		this.price3 = price3;
	}

	public boolean isPrice4() {
		return price4;
	}

	public void setPrice4(boolean price4) {
		this.price4 = price4;
	}

}
