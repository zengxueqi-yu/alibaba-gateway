package com.july.gateway.exception;

import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

/**
 * 所有业务异常 程序异常编码是小于0 业务异常编码是大于0 不适用等于0的编码
 * @author zengxueqi
 * @since 2020/4/20
 */
public class BnException extends RuntimeException {

    private static final long serialVersionUID = -1494138156106032736L;
    /**
     * 属性对象
     */
    private static final Properties prop = new Properties();
    /**
     * 当没有指定编码时抛出异常编码99999
     */
    public static final Integer ERR_CODE = 99999;
    /**
     * 错误提示
     */
    public static final String ERR_MESG = "业务错误";
    /**
     * 错误编码10000
     */
    public static final Integer NONNULL = 10000; // 不允许为空
    /**
     * 错误编码10001
     */
    public static final Integer NONBLANK = 10001; // 不允许长度为0

    /**
     * 异常编码,默认编码99999
     */
    private Integer code = ERR_CODE;

    static {
        try {
            prop.load(BnException.class.getResourceAsStream("/errors.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认构造函数
     */
    public BnException() {
        super();
    }

    /**
     * 构造函数
     * @param code               错误编码
     * @param message            消息
     * @param cause              导致异常
     * @param enableSuppression  允许抑制
     * @param writableStackTrace 写堆栈
     */
    public BnException(Integer code, String message, Throwable cause, boolean enableSuppression,
                       boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    /**
     * 构造函数
     * @param message 消息
     * @param cause   导致原因
     */
    public BnException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     * @param cause 导致原因
     */
    public BnException(Throwable cause) {
        super(cause);
    }

    /**
     * 根据异常编码和消息构建对象
     * @param code    编码
     * @param message 消息
     */
    public BnException(Integer code, String message) {
        super(message);
        if (code == null) code = ERR_CODE;
        if (message == null) message = ERR_MESG;
        this.code = code;
    }

    /**
     * 根据异常编码构建对象
     * @param code 编码
     */
    public BnException(Integer code) {
        this(code, ERR_MESG);
    }

    /**
     * 根据异常编码构建对象
     * @param message 消息
     */
    public BnException(String message) {
        this(ERR_CODE, message);
    }

    /**
     * 返回异常编码
     * @return 返回异常编码
     */
    public Integer code() {
        return this.code;
    }

    /**
     * 返回异常消息,如果错误消息为空则返回全堆栈信息
     * @return 返回异常消息
     */
//	public String message() {
//		String msg=super.getMessage();
//		if(msg==null || "null".equals(msg) || msg.trim().length()<=0) {
//			try(StringWriter sw = new StringWriter();
//				 PrintWriter pw = new PrintWriter(sw)) {
//				this.printStackTrace(pw);
//				return sw.toString();
//			} catch (Exception ex) {
//				return ex.getMessage();
//			}
//		}
//		return msg;
//	}
    public String stackTrace() {
        return stackTrace(this);
    }

    /***
     * 输出异常的堆栈的关键信息
     * @param ex 异常类型
     * @return 异常类型的字符串
     * @author cqyhm
     */
    public static String stackTrace(Exception ex) {
        try (StringWriter out = new StringWriter();
             PrintWriter printWriter = new PrintWriter(out)) {
            ex.printStackTrace(printWriter);
            String[] liners = out.toString().split("\n");
            StringBuffer sb = new StringBuffer();
            boolean flag = false; //包含异常的标志
            for (String line : liners) {
                if (line == null) {
                    continue;
                }
                if (line.trim().contains("Exception:") || line.trim().endsWith("Exception")) {
                    sb.append(line.trim()).append(" ");
                    flag = true;
                    continue;
                }
                if (flag && line.trim().length() > 0) {
                    sb.append(line.trim()).append("\n");
                    flag = false;
                }
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "未知异常:" + e.toString().trim();
        }
    }

//	@Override
//	public String getMessage() {
//		return this.message();
//	}

    /**
     * 使用给的参数生成业务异常
     * 占位符必须是{0},{1},{2}等等,如果写成{}的话会出错。
     * @param code             异常编码
     * @param message(pattern) 异常消息,带有占位符的消息
     * @param args             异常消息占位参数, 依次对应消息中的{0},{1},{2}等等
     * @return BnException
     */
    private static BnException on(Integer code, String message, Object... args) {
        if (code == null && StringUtils.isEmpty(message)) {
            return new BnException("on(code/message/args)中code和message不允许都为空");
        }
        //检查占位符是否错误。
        if (message != null && message.contains("{}")) {
            return new BnException("BnException中message模式的占位符必须是{0},{1},{2}格式.");
        }
        //消息优先，没有指定消息时根据code从配置文件读取
        if (code != null) {
            if (StringUtils.isEmpty(message)) {
                message = prop.getProperty("" + code, "在errors.properties找不到错误编码" + code);
            }
        } else {        //如果code一直为空则重置为默认值
            code = ERR_CODE;
        }
        return new BnException(code, MessageFormat.format(message, args));
    }

    /**
     * 转换指定的异常
     * @param cause 异常类
     * @return 返回标准异常
     */
    public static BnException on(Throwable cause) {
        return new BnException(cause);
    }

    /**
     * 检查指定的条件,满足则抛出异常.
     * @param checked 抛出异常的条件为true时抛出异常
     * @param code    异常编码
     * @param message 异常消息
     * @param args    异常消息占位参数
     */
    private static void of(boolean checked, Integer code, String message, Object... args) {
        if (checked)
            throw on(code, message, args);
    }

    /**
     * 检查指定的条件,满足则抛出异常,并可以指定参数.
     * @param checked 检查条件
     * @param code    消息编码
     * @param args    异常消息占位参数
     */
    public static void of(boolean checked, Integer code, Object... args) {
        of(checked, code, null, args);
    }

    /**
     * 检查指定的条件,满足则抛出异常,并可以指定参数. code 默认为99999
     * @param checked 检查条件
     * @param message 消息
     * @param args    异常消息占位参数
     */
    public static void of(boolean checked, String message, Object... args) {
        of(checked, null, message, args);
    }

    /**
     * 抛出指定消息的异常,可以指定参数. code 默认为99999
     * @param message 异常消息内容
     * @param args    异常消息占位参数
     */
    public static void of(String message, Object... args) {
        of(true, null, message, args);
    }

    /**
     * 抛出指定消息的异常. 消息内容从配置文件中获取.
     * @param code 消息编码
     * @param args 占位参数
     */
    public static void of(Integer code, Object... args) {
        of(true, code, null, args);
    }

    /**
     * 使用给的参数生成业务异常 消息使用默认消息、无占位参数
     * 使用默认消息，但默认消息中无占位符,故这里不需要传递参数
     * @param code 异常编码,必须是存在errors.properties中的编码
     * @param args 占位参数,依次对应消息中的{0},{1},{2}等等
     * @return BnException 异常对象
     */
    public static BnException on(Integer code, Object... args) {
        return on(code, null, args);
    }

    /**
     * 使用给的参数生成业务异常 code 默认为99999
     * @param message(pattern) 异常消息，可包含占位符{0},{1},{2}等
     * @param args             消息中的占位参数,依次对应消息中的{0},{1},{2}等等
     * @return BnException 异常对象
     */
    public static BnException on(String message, Object... args) {
        return on(null, message, args);
    }

    /**
     * 指定的对象为null,则抛出异常 固定错误码: 10000
     * @param nullChecked 待检查的对象
     * @param args        异常消息占位参数
     * @param <T>         数据类型
     */
    public static <T> void ofNull(T nullChecked, Object... args) {
        of(Objects.isNull(nullChecked), NONNULL, null, args);
    }

    /**
     * 指定的对象为null或长度为0,则抛出异常 固定错误码: 10001
     * @param blankChecked 待检查的对象
     * @param args         异常消息占位参数
     * @param <T>          数据类型
     */
    public static <T> void ofBlank(T blankChecked, Object... args) {
        of(Objects.isNull(blankChecked) || blankChecked.toString().trim().length() == 0, NONBLANK, null, args);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "BnException:[code=" + code() + ", message=" + getMessage() + "]";
    }
}
