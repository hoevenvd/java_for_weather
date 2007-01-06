JAVA_HOME=/usr/java/jdk1.5.0_05

PATH=$JAVA_HOME/bin:$PATH

java -noverify -cp lib/spring.jar:lib/commons-logging.jar:lib/log4j.jar:classes:lib/mysql-connector-java.jar:lib/comm.jar:lib/commons-collections.jar:lib/commons-dbcp.jar:lib/commons-pool.jar:lib/jcommon.jar:lib/jfreechart.jar -Dlog4j.configuration=file:///home/tom/wx/weather/old-log4j.properties org.tom.weather.davis.wm2.WeatherMonitor weather.properties 
