package com.cmbc.devops.bean;

import java.util.List;

/**
 * 当前页数据信息类
 * @author dmw
 *
 */
public class GridBean {

	private int page; //当前页
	private int total; //总页数
	private int records;//总条数
	private List<?> rows;//当前页数据

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}

	public List<?> getRows() {
		return rows;
	}

	public void setRows(List<?> rows) {
		this.rows = rows;
	}

	public GridBean(int page, int total, int records, List<?> rows) {
		super();
		this.page = page;
		this.total = total;
		this.records = records;
		this.rows = rows;
	}
}
