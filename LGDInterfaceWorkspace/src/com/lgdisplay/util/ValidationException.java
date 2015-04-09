/*
 * 작성된 날짜: 2007-01-22
 *
 * TODO 생성된 파일에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
 */

package com.lgdisplay.util;

/**
 * @author db2admin
 *
 * TODO 생성된 유형 주석에 대한 템플리트를 변경하려면 다음으로 이동하십시오.
 * 창 - 환경 설정 - Java - 코드 스타일 - 코드 템플리트
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
