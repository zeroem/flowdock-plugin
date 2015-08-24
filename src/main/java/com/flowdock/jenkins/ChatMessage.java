package com.flowdock.jenkins;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import java.io.UnsupportedEncodingException;

public class ChatMessage extends FlowdockMessage {
    protected String externalUserName;

    public ChatMessage() {
        this.externalUserName = "Jenkins";
    }

    public void setExternalUserName(String externalUserName) {
        this.externalUserName = externalUserName;
    }

    public String asPostData() throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();
        postData.append("content=").append(urlEncode(content));
        postData.append("&external_user_name=").append(urlEncode(externalUserName));
        postData.append("&tags=").append(urlEncode(removeWhitespace(tags)));
        return postData.toString();
    }

    public static ChatMessage fromBuild(AbstractBuild build, BuildResult buildResult, BuildListener listener) {
        ChatMessage msg = new ChatMessage();
        StringBuffer content = new StringBuffer();

        String projectName = "";
        String configuration = "";
        if(build.getProject().getRootProject() != build.getProject()) {
            projectName = build.getProject().getRootProject().getName();
            configuration = " on " + build.getProject().getName();
        } else {
            projectName = build.getProject().getName();
        }

        String buildNo = build.getDisplayName().replaceAll("#", "");
        content.append(projectName + configuration).append(" build ").append(buildNo);
        content.append(" ").append(buildResult.getHumanResult());

        String rootUrl = Hudson.getInstance().getRootUrl();
        String buildLink = (rootUrl == null) ? null : rootUrl + build.getUrl();
        if(buildLink != null) content.append(" \n").append(buildLink);

        msg.setContent(content.toString());
        return msg;
    }
}
