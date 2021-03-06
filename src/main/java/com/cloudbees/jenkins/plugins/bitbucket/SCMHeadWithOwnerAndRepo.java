/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cloudbees.jenkins.plugins.bitbucket;

import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequestDestination;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequestSource;
import com.cloudbees.jenkins.plugins.bitbucket.server.client.pullrequest.BitbucketServerPullRequest;
import com.cloudbees.jenkins.plugins.bitbucket.server.client.pullrequest.BitbucketServerPullRequestDestination;
import com.cloudbees.jenkins.plugins.bitbucket.server.client.pullrequest.BitbucketServerPullRequestSource;
import com.cloudbees.jenkins.plugins.bitbucket.server.client.repository.BitbucketServerRepository;
import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.ObjectStreamException;
import java.net.MalformedURLException;
import java.net.URL;

import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketPullRequest;

import jenkins.scm.api.SCMHead;
import jenkins.scm.api.actions.ChangeRequestAction;

/**
 * Legacy class retained to allow for graceful migration of serialized data.
 * @deprecated use {@link PullRequestSCMHead} or {@link BranchSCMHead}
 */
@Deprecated
public class SCMHeadWithOwnerAndRepo extends SCMHead {

    private static final long serialVersionUID = 1L;

    private final String repoOwner;

    private final String repoName;

    private transient PullRequestAction metadata;

    public SCMHeadWithOwnerAndRepo(String repoOwner, String repoName, String branchName) {
        super(branchName);
        this.repoOwner = repoOwner;
        this.repoName = repoName;
    }

    private Object readResolve() throws ObjectStreamException {
        if (metadata != null) {
            // we just want to flag this as a PR, the legacy data did not contain the required information so
            // we will end up triggering a rebuild on next index / event via take-over
            return new PullRequestSCMHead(repoOwner, repoName, getName(), metadata.getId(), new BranchSCMHead("\u0000"));
        }
        return new BranchSCMHead(getName());
    }

}
