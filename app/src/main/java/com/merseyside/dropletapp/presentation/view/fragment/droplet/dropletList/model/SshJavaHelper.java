//package com.merseyside.dropletapp.presentation.view.fragment.droplet.dropletList.model;
//
//import java.io.InputStream;
//import java.util.Properties;
//
//public class SshJavaHelper {
//
//    Session session;
//    Channel channel;
//
//    boolean openSshConnection(String host, String username, String filePathPrivate, String filePathPublic) {
//
//        try {
//            JSch jsch = new JSch();
//
//            jsch.addIdentity(filePathPrivate);
//
//            session = jsch.getSession(username, host, 22);
//
//            session.setTimeout(10000);
//
//            Properties properties = new Properties();
//
//            properties.put("StrictHostKeyChecking", "no");
//
//            session.setConfig(properties);
//            session.connect();
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return false;
//        }
//    }
//
//    String getOvpnFile(String script) {
//
//        try {
//            if (channel == null) {
//                channel = session.openChannel("exec");
//                channel.connect();
//            }
//
//            ((ChannelExec) channel).setCommand(script);
//
//            InputStream in = channel.getInputStream();
//
//            byte[] tmp=new byte[1024];
//            while(true){
//                while(in.available()>0){
//                    int i=in.read(tmp, 0, 1024);
//                    if(i<0)break;
//                    System.out.print(new String(tmp, 0, i));
//                }
//                if(channel.isClosed()){
//                    if(in.available()>0) continue;
//                    System.out.println("exit-status: "+channel.getExitStatus());
//                    break;
//                }
//                try{Thread.sleep(1000);}
//                catch(Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            return "kek";
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            return "wker";
//        }
//    }
//}
