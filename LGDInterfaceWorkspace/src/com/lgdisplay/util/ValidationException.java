/*
 * �ۼ��� ��¥: 2007-01-22
 *
 * TODO ������ ���Ͽ� ���� ���ø�Ʈ�� �����Ϸ��� �������� �̵��Ͻʽÿ�.
 * â - ȯ�� ���� - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */

package com.lgdisplay.util;

/**
 * @author db2admin
 *
 * TODO ������ ���� �ּ��� ���� ���ø�Ʈ�� �����Ϸ��� �������� �̵��Ͻʽÿ�.
 * â - ȯ�� ���� - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */
public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ValidationException() {

	}
	public ValidationException(Throwable t) {

	}
	public ValidationException(Throwable t, String errcd) {

	}
	public ValidationException(String msg) {
		super(msg);
	}
	public ValidationException(Throwable t, String errcd, String msg) {

	}
	public ValidationException(String errcd, String msg) {

	}
}
