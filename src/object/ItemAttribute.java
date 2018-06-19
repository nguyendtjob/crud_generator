package object;

public class ItemAttribute {
	private String name;
	private String type;
	private String tooltip;
	private String filter;

	public ItemAttribute(String name, String type, String tooltip) {
		this.name = name;
		this.type = type;
		this.tooltip = tooltip;
		this.filter = "column_filter";
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getTooltip() {
		return tooltip;
	}

	public String getFilter() {
		return this.filter;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setFilter(String strict) {
		this.filter = strict;
	}

}
