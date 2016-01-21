package com.ztm;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ztm.TestAndroidActivity.MyURLSpan;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;

public class StringUtil {
	
	
	static boolean isNext;
	static String curAreaName;
	static boolean topicWithImg = false;
	static int pageNum;
	static String topicUrl;
	static String actitle;
	static HashMap<String,String> picTempFiles;
	static HashMap<String, Integer> smilyAll;
	

	static Pattern mPattern = Pattern.compile("\\[(;|:).{1,4}\\]");
	static Pattern colorPat = Pattern.compile("\\[(1;.*?|37;1|32|33)m");
	//static Pattern rePat = Pattern.compile("\\[(1;.*?|37;1|32|33)m");
   

	public static HashMap<String, String> fFolorAll = null;
	public static void initAll()
	{
		fFolorAll = BBSAll.getFColorAll();
		picTempFiles = new HashMap<String,String>();
		smilyAll = BBSAll.getSmilyAll();
	}
	
	public static String[] getArray(HashMap<String, String> bbsAll2) {
		String[] ba = new String[bbsAll2.size() * 2];
		int i = 0;
		Set<String> keySet = bbsAll2.keySet();
		for (String string : keySet) {
			ba[i] = string;
			ba[i + 1] = bbsAll2.get(string);
			i += 2;
		}
		return ba;
	}
	

	private static  boolean isEnglish(char c) {
		if ((c >= 0 && c <= 9) || (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'z') || c == ' ') {
			return true;
		} else
			return false;
	}
	
	public static Spanned getSmilyStr(String string,Resources res ) {
		final Resources resou = res;
		 return Html.fromHtml(string.replaceAll("<", " <").replaceAll(">", "> "),
					new Html.ImageGetter() {
						public Drawable getDrawable(String source) {

							Drawable drawable = null;
							if (source.startsWith("[")) {
								try {
									
									Integer i = smilyAll.get(source);
									if (i != null) {
										drawable = resou.getDrawable(i);
									} 
								} catch (Exception e) {
									return null;
								}
								if (drawable == null)
									return null;
								int iw = drawable
										.getIntrinsicWidth();
								drawable
										.setBounds(
												0,
												0,
												iw,
												drawable
														.getIntrinsicHeight());
							}
							return drawable;

						}
					}, null);
		}
	
	
	public static String getStrBetter(String string) {

		String scon = string;// new
		String[] split = scon.split("\n");
		StringBuilder allSb = new StringBuilder();
		for (String sp : split) {
			if(sp.contains("��")||sp.startsWith("http://")) 
			{
				allSb.append(sp+'\n');
				continue;
			}
			
			if(sp.startsWith(":")) 
			{
				if(sp.length()>40)
				{
					sp=sp.substring(0,40)+"...";
				}
				allSb.append(sp+"\n");
				
				continue;
			}
			
			StringBuilder sb = new StringBuilder(sp);
			int len = sp.length();
			int tempLen = 0;
			for (int i = 0; i < len; i++) {
				char charAt = sb.charAt(i);
				if (isEnglish(charAt)) {
					tempLen++;
				} else {
					tempLen += 2;
				}
				if (tempLen >= 80) {
					sb.insert(i + 1, '\n');
					len++;
					i++;
					tempLen = 0;
				}
			}

			allSb.append(sb.append('\n'));
		}
		string = allSb.toString();
		return string;
	}
	
	public static String getSpanedString(String data,String first,String last,int lastInt)
	{
		String ret = "";
		int indexOf = data.lastIndexOf(first);
		int indexOf2 = data.lastIndexOf(last);
		if (indexOf>0&&indexOf2>0)
		{
			ret = data.substring(indexOf+first.length(),indexOf2+lastInt);
		}
		return ret;
	}

	
	 public static String ToSBC(String input) {
		  // ���תȫ�ǣ�
		  char[] c = input.toCharArray();
		  for (int i = 0; i < c.length; i++) {

		   if (c[i] == 32) {
		    c[i] = (char) 12288;
		    continue;
		   }
		   if (c[i] < 127 && c[i]>32)
		    c[i] = (char) (c[i] + 65248);

		  }
		  return new String(c);
		 }


	
	
	/**
	 * ������ȡ��ҳ�� ����ĳ���ض��Ļ���
	 * 
	 * @param data
	 * @return
	 */
	public static String getTopicInfo(String data,int nowPos,String nowLoginId,boolean isTopic) {
		StringBuffer tiList = new StringBuffer("<br>");
		char s = 10;
		int nowPosForNext = nowPos;
		String backS = s + "";
		topicWithImg = false;
		data = data.replaceAll(backS, "<br>");
		
		ConstParam.tpList = new ArrayList<TopicPics>();
		int curNo = 1;
		
		//�����ظ�����   ����<a href='bbspst?board=Pictures&file=M.1323243608.A'>�ظ�����</a>
		
		Pattern rePat = Pattern.compile("bbspst\\?.*?\\.A"); 
		        Matcher matcher = rePat.matcher(data);
		       List<String> reList = new ArrayList<String>();
		      
		        while (matcher.find()) {
		        	reList.add(matcher.group());
		        	
		        }
		        
		        pageNum = -1;
		int lastIndexOf = data.lastIndexOf(" ƪ����.");
		if(lastIndexOf>0)
		{
			String substring = data.substring(lastIndexOf-6, lastIndexOf);
			int indexOf = substring.indexOf(" ");
			if (indexOf>0)
			{
				String Num = substring.substring(indexOf+1);
				pageNum = Integer.parseInt(Num);
			}
		}
		
		topicUrl = "";
		if(!isTopic)
		{
			lastIndexOf = data.lastIndexOf("' >ͬ�����Ķ�</a>");
			if(lastIndexOf>0)
			{
				String substring = data.substring(lastIndexOf-80, lastIndexOf);
				int indexOf = substring.lastIndexOf("<a href='");
				if (indexOf>0)
				{
					topicUrl = substring.substring(indexOf+9);
				}
			}
		}
		
		        
		
		
		rePat = Pattern.compile("class=hide>.*?</textarea></table>"); 
        matcher = rePat.matcher(data);
        
	
       // Log.d("1", new java.util.Date().getTime()+"");
		String lz = "";
		int k = 0;
		  while (matcher.find()) {
			
			  String sss = matcher.group();
	        	String text = sss.substring(11,sss.length()-20);
			String content = "";
			String userId ="";
			content = text;
			String nowP = "";
			
			if(nowPos<0) nowPos=0;
			{
				nowP = (nowPos + k) + "";
				if (k == 0) {
					nowP = "0";
				}

			}
			
			 if(k==0) {
				 int areaNo = text.indexOf("����:");
					int titleNo = text.indexOf("<br>��"); 
					if(areaNo>0&&titleNo>0)
					{
				 String area =text.substring(areaNo+4, titleNo); 
				 curAreaName =  area.replaceAll("<br>", ""); 
				 int indexOf = curAreaName.indexOf('.');
				 if(indexOf>-1)
				 {
					 curAreaName = curAreaName.substring(0,indexOf);
				 }
					 
				 curAreaName = curAreaName.toLowerCase();
				 curAreaName = curAreaName.replaceFirst(
						 curAreaName.substring(0, 1),
						 curAreaName.substring(0, 1)
									.toUpperCase());
					}
					else
					{
						curAreaName = "byztm";
					}
				 
				 
			 }
			k++;
			String nbs = "<br>";// &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

			if (k == 1 && nowPos > 1) {
				int indexOf = content.indexOf("����վ:");
				if(indexOf<0)
				{
					content="<br><br>(����ʡ��)<br>";
				}
				else
				{
					
				int indexOf2 = content.indexOf("������:");
				
				
				
				if(indexOf2==0)
				{
					content = content.substring(4, content.indexOf("����վ:"))
					+ "<br><br>(����ʡ��)<br>";
				
					int ind = content.indexOf(" (");
					userId = content.substring(1, ind);
					lz = " "+userId;
				}
				else
				{
					content = content.substring(0, content.indexOf("����վ:"))
					+ "<br><br>(����ʡ��)<br>";
				}
				
				}
			} else {
				/** * ����Ӳ�س� */
				String[] split = content.split("<br>");

				int j = 0;
				int tempBr = 0;
				StringBuffer sb = new StringBuffer();
				for (String sconA : split) {
					
					if (j < 3) {
						j++;
						if (j == 1) {
							if(!sconA.startsWith("������:"))
							{
								 sb.append(sconA).append(nbs);
								 continue;
							 }
							sconA = sconA.substring(4);
							int ind = sconA.indexOf(" (");
							int inArea = sconA.indexOf(", ����");
							if(ind<0||inArea<0) break;
							
							userId = sconA.substring(1, ind);
							String DisId = " "+userId;
							if(userId.equalsIgnoreCase(nowLoginId)) DisId = " ��";
								
							if (k == 1) {
								lz = DisId;
								
								sb.append("<a href='http://bbs.nju.edu.cn/bbsqry?userid="+userId+"'><font color=#0000EE >").append(
										DisId).append(
										"</font></a>").append(
										sconA.substring(ind, inArea)).append(
										nbs)
										.append(sconA.substring(inArea + 1)).append(
												nbs);
										;
								
							} else {
								
								if(lz.equals(DisId))
								{
									DisId=" ¥��";
								}
								
								sb.append("<a href='http://bbs.nju.edu.cn/bbsqry?userid="+userId+"'><font color=#0000EE >").append(
										DisId).append(
										"</font></a>").append(
										sconA.substring(ind, inArea)).append(
												nbs);

							}
							
							continue;
						}
						if (j == 2)
						{
							if(k != 1)
							continue;
							else
							{
								if(sconA.startsWith("��")) 
								 {
									 actitle = sconA.substring(5).toString();
								 }
								else
								{
									 actitle = "";
								}
							}
						}
						 if(j==3)
						 {
							 if(!sconA.startsWith("����վ:")) 
							 {
								 sb.append(sconA).append(nbs);
								 continue;
							 }
							 if(sconA.length()<16) break;
							 sconA =  sconA.substring(15,sconA.length()-1);
							 String date = DateUtil.formatDateToStr(DateUtil.getDatefromStr(sconA));
								if(date == null)
									sconA ="�����ڣ�"+sconA;		
								else
									sconA ="�����ڣ�"+date;
							 sconA+=nbs;
							 tempBr = 1;
						 }

						sb.append(sconA).append(nbs);
						
						continue;
					} else if (sconA.length() < 1) {
						if (tempBr == 0) {
							tempBr = 1;
							sb.append(sconA).append(nbs);
						}
						continue;
					}

					if (sconA.contains("��Դ:��"))
					{
						if(ConstParam.isIP)
						{
							int ipIndex = sconA.indexOf("[FROM:");
							if(ipIndex>0)
							{
								String ip = sconA.substring(ipIndex+7);
								String ss = fFolorAll.get("[1;3"+(k%6+1)+"m");
								sb.append(ss).append("<br>��Դ:��").append(ip.substring(0,ip.indexOf(']'))).append("</font><br>");
							}
						}
						continue;
					}
					if	(sconA.contains("�޸�:��")) {
						continue;
					}
					if	(sconA.equals("--")) {
						
						if(!isTopic)
							sb.append(sconA+ nbs);
						
						continue;
							
					}
					
					if(sconA.startsWith(":"))
					{
						sb.append("<font color=#808080>"+sconA+"</font>"+ nbs);
						continue;
					}
					
					sconA = sconA.trim();
					
						if (sconA.toLowerCase().startsWith("http:")
								&& (sconA.toLowerCase().endsWith(".jpg") 
										|| sconA.toLowerCase().endsWith(".png")
										||sconA.toLowerCase().endsWith(".jpeg")
										||sconA.toLowerCase().endsWith(".gif")
										))
						{
							
							TopicPics tp  = new TopicPics();
							
							tp.setNo(curNo);
							curNo++;
							tp.setLink(sconA);
							
							ConstParam.tpList.add(tp);
							
							
							if(ConstParam.isPic.equals(Const.AllPic)||(	ConstParam.isWifi &&ConstParam.isPic.equals(Const.WIFIPic)))

							{
								sb.append("<a href='"+sconA+"'><img src='").append(sconA).append("'></a><br>");
								topicWithImg = true;
								continue;
							}
							else
							{
								sb.append("<a href='"+sconA+"'><img src='nopic'></a><br>");
								continue;
							}
							
							
								
						
						}
					

					tempBr = 0;
					int la = 0;
					try {
						la = sconA.getBytes("gb2312").length;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					//sconA = ToSBC(sconA);
					if (la < 71 || la > 89) {
						sb.append(sconA + nbs);
					} else {
						sb.append(sconA);
					}

				}
				if(sb.length()>0)
					content = sb.toString();
			}
			String sFL;
			if (nowP == "0") {
				sFL = "¥��:";
			} else {
				sFL = nowP+"¥:";
			}
			if(userId.length()>1&&userId.equalsIgnoreCase(nowLoginId))
			{
				if(reList.size()>0)
				{
				tiList.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1)+"'>[<font color=#0000EE >�ظ�</font>]</a>&nbsp;&nbsp;")
				.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1).replace("bbspst?", "bbsedit?")+"'>[<font color=#0000EE>�޸�</font>]</a>&nbsp;&nbsp;")
				.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1).replace("bbspst?", "bbsdel?")+"'>[<font color=#0000EE>ɾ��</font>]</a><br>");
				}
				tiList.append(sFL)
				.append(content)
				
				.append("</font>")
				
				.append("<img src='xian'><br><br>");
			}
			else
			{
				if(reList.size()>0&&k<=reList.size())
				{
				tiList.append("<a href='http://bbs.nju.edu.cn/"+reList.get(k-1)+"'>[<font color=#0000EE >�ظ�</font>]</a>");
				}
				
				tiList.append(sFL).append(content).append(
				"</font><img src='xian'><br><br>");
			}
			
			
		}
		  
			if (k < 31) {
				isNext = false;
			} else {
				isNext = true;
			}
			// Log.d("2", new java.util.Date().getTime()+"");
		String ss = tiList.toString()
//		+"<a href='curArea'>[<font color=#0000EE >��������</font>]</a>&nbsp;&nbsp;"
//		+"<a href='prev'>[<font color=#0000EE >��һҳ</font>]</a>&nbsp;&nbsp;"
		
//		+"<a href='huifu'>[<font color=#0000EE >�ظ�</font>]</a>"
		;
		
		if (isNext&&nowPosForNext!=-1) {
			ss+="&nbsp;<a href='next'>[<font color=#0000EE >�����30���ظ�</font>]</a><br>";
		}
		
		return addSmileySpans(ss,null);
	}

	
	
    public static String addSmileySpans(String text,String sign) {
    	//�滻����
        Matcher matcher = mPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
        	matcher.appendReplacement(sb, "<img src=\""+matcher.group()+"\">");
        }
        matcher.appendTail(sb);
        //�滻������ɫ
       
        
        if(sign!=null)
        {
        	 int indexOf = sb.indexOf(sign);
        	 if(indexOf>0)
        	 {
        		 String signColor = sb.substring(indexOf-7, indexOf) ;
        		 String sex = "";
        		 if(signColor.contains("36"))
        		 {
        			 sex = " - ����";
        		 }
        		 else  if(signColor.contains("35"))
        		 {
        			 sex = " - Ů��";
        		 }
        		 else
        		 {
        			 sex = " - δ֪";
        		 }
        		 sb.insert(indexOf+1, sex);
        	 }
        }
        StringBuffer sb2= new StringBuffer();
        
        matcher = colorPat.matcher(sb);
        while (matcher.find()) {
        	String ss = fFolorAll.get(matcher.group(0));
        	if(ss==null)
        		ss = "";
        	matcher.appendReplacement(sb2,ss );
        }
        matcher.appendTail(sb2);
       
        
        return sb2.toString().replaceAll("\\[\\+reset\\]|\\[m|\\[([0-9]{1,2};)*[0-9]{1,2}m", "</font>");
    }
    
	
    public static String getBetterTopic(String infoView)
	{
		String nbs = "<br>";
		String content="";
		String[] split = infoView.split(nbs);
		StringBuffer sb = new StringBuffer();
		int tempBr=0;
		for (String sconA : split) {
			if (sconA.length() < 1) {
				if (tempBr == 0) {
					tempBr = 1;
					sb.append(sconA).append(nbs);
				}
				continue;
			}
			
			sconA = sconA.trim();
			
			
			
			if (sconA.contains("��Դ:��")||sconA.contains("�޸�:��")||sconA.contains("��:")
					)
			{
				continue;
			}
			if(sconA.startsWith("[Head]"))
			{
				sb.append("[1;34m"+sconA.substring(6)+"[m"+ nbs);
				continue;
			}
			if(sconA.startsWith(":"))
			{
				sb.append("<font color=#808080>"+sconA+"</font>"+ nbs);
				continue;
			}
			

			tempBr = 0;
			String scon = "";
			try {
				scon = new String(sconA.getBytes("gb2312"),
						"iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (scon.length() < 71 || scon.length() > 89) {
				sb.append(sconA + nbs);
			} else {
				sb.append(sconA);
			}

		}
		
		if(sb.length()>0)
			content = sb.toString();

	
		return content;
		
	}
	
   

	/**
	 * ������ȡ��ҳ�� ����10���б�
	 * 
	 * @param data
	 * @return
	 */
	public static List<TopicInfo> getTop10Topic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		if (tds.size() != 55) {
			TopicInfo ti = new TopicInfo();
			ti.setTitle("�������������!");
			tiList.add(ti);
			return tiList;
		}

		for (int i = 1; i < 11; i++) {
			int pos = i * 5;
			TopicInfo ti = new TopicInfo();
			ti.setRank(i + "");
			ti.setLink((tds.get(pos + 2).getElementsByTag("a")).get(0).attr(
					"href"));// ����title
			ti.setTitle("�� "+tds.get(pos + 2).text());// ����title
			ti.setArea(tds.get(pos + 1).text());
			ti.setNums(tds.get(pos + 4).text());
			ti.setAuthor(tds.get(pos + 3).text());
			tiList.add(ti);
		}

		return tiList;
	}



}
