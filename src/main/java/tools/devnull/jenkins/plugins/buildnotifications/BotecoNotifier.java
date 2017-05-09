/*
 * The MIT License
 *
 * Copyright (c) 2016 Marcelo "Ataxexe" Guimarães <ataxexe@devnull.tools>
 *
 * ----------------------------------------------------------------------
 * Permission  is hereby granted, free of charge, to any person obtaining
 * a  copy  of  this  software  and  associated  documentation files (the
 * "Software"),  to  deal  in the Software without restriction, including
 * without  limitation  the  rights to use, copy, modify, merge, publish,
 * distribute,  sublicense,  and/or  sell  copies of the Software, and to
 * permit  persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this  permission  notice  shall be
 * included  in  all  copies  or  substantial  portions  of the Software.
 *                        -----------------------
 * THE  SOFTWARE  IS  PROVIDED  "AS  IS",  WITHOUT  WARRANTY OF ANY KIND,
 * EXPRESS  OR  IMPLIED,  INCLUDING  BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN  NO  EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM,  DAMAGES  OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT  OR  OTHERWISE,  ARISING  FROM,  OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE   OR   THE   USE   OR   OTHER   DEALINGS  IN  THE  SOFTWARE.
 */

package tools.devnull.jenkins.plugins.buildnotifications;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * A notifier that uses Boteco to delivery messages
 *
 * @author Ataxexe
 */
public class BotecoNotifier extends BaseNotifier {

  /**
   * Creates a new notifier based on the given parameters
   *
   * @param globalTarget      the target for all notifications
   * @param successfulTarget  the target for build success notifications
   * @param brokenTarget      the target for broken build notifications
   * @param stillBrokenTarget the target for still broken build notifications
   * @param fixedTarget       the target for fixed build notifications
   * @param sendIfSuccess     if the notification should be sent if the build succeed
   */
  @DataBoundConstructor
  public BotecoNotifier(String globalTarget,
                        String successfulTarget,
                        String brokenTarget,
                        String stillBrokenTarget,
                        String fixedTarget,
                        boolean sendIfSuccess) {
    super(globalTarget, successfulTarget, brokenTarget, stillBrokenTarget, fixedTarget, sendIfSuccess);
  }

  @Override
  protected Message createMessage(String target, AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
    BotecoDescriptor descriptor = (BotecoDescriptor) getDescriptor();
    return new BotecoMessage(target, descriptor.endpoint);
  }

  /**
   * The descriptor for the BotecoNotifier plugin
   */
  @Extension
  public static class BotecoDescriptor extends BuildStepDescriptor<Publisher> {

    private String endpoint;

    public BotecoDescriptor() {
      load();
    }

    public String getEndpoint() {
      return endpoint;
    }

    public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
      JSONObject config = json.getJSONObject("boteco");
      this.endpoint = config.getString("endpoint");
      save();
      return true;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
      return true;
    }

    @Override
    public String getDisplayName() {
      return "Boteco Notification";
    }

  }

}
