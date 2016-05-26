package com.cmbc.devops.component;

public class CaptchaNumber {
	
	private Integer firNum;
	private Integer secNum;
	private Integer totalNum;
	
	/**
	 * @author langzi
	 */
	public CaptchaNumber() {

	}
	
	/**
	 * @author langzi
	 * @param firNum
	 * @param secNum
	 * @param totalNum
	 */
	public CaptchaNumber(Integer firNum, Integer secNum, Integer totalNum) {
		super();
		this.firNum = firNum;
		this.secNum = secNum;
		this.totalNum = totalNum;
	}
	
	public Integer getFirNum() {
		return firNum;
	}
	
	public void setFirNum(Integer firNum) {
		this.firNum = firNum;
	}
	
	public Integer getSecNum() {
		return secNum;
	}
	
	public void setSecNum(Integer secNum) {
		this.secNum = secNum;
	}
	
	public Integer getTotalNum() {
		return totalNum;
	}
	
	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	@Override
	public String toString() {
		return "CaptchaNumber [firNum=" + firNum + ", secNum=" + secNum
				+ ", totalNum=" + totalNum + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firNum == null) ? 0 : firNum.hashCode());
		result = prime * result + ((secNum == null) ? 0 : secNum.hashCode());
		result = prime * result
				+ ((totalNum == null) ? 0 : totalNum.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaptchaNumber other = (CaptchaNumber) obj;
		if (firNum == null) {
			if (other.firNum != null)
				return false;
		} else if (!firNum.equals(other.firNum))
			return false;
		if (secNum == null) {
			if (other.secNum != null)
				return false;
		} else if (!secNum.equals(other.secNum))
			return false;
		if (totalNum == null) {
			if (other.totalNum != null)
				return false;
		} else if (!totalNum.equals(other.totalNum))
			return false;
		return true;
	}
	
}
