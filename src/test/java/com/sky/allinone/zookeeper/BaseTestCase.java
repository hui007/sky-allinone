/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sky.allinone.zookeeper;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(BaseTestCase.class);
    
    int port;
    File tmpDir;
    final static File BASETEST =
            new File(System.getProperty("buildTestDir", BaseTestCase.class.getResource("").getPath() + "buildTestDir"));
    
    /**
     * Creates a temporary directory.
     * 
     * @return
     * @throws IOException
     */
    @Test
    public void createTmpDir() throws IOException {
    	LOG.info("buildTestDir:{}", BASETEST.getAbsolutePath());
    	LOG.info("编译后的文件根目录:{}", this.getClass().getResource("/").getPath());
    	LOG.info("类全名称:{}", this.getClass().getCanonicalName());
    	LOG.info("当前类编译后所在目录:{}", this.getClass().getResource("").getPath());
    	LOG.info("当前类编译后所在目录:{}", BaseTestCase.class.getResource("").getPath());
        createTmpDir(BASETEST);
    }
    
    /**
     * Creates a temporary directory under a base directory.
     * 
     * @param parentDir
     * @return
     * @throws IOException
     */
    static File createTmpDir(File parentDir) throws IOException {
        File tmpFile = File.createTempFile("test", ".junit", parentDir);
        LOG.info("tmpFile:{}", tmpFile.getAbsolutePath());
        // don't delete tmpFile - this ensures we don't attempt to create
        // a tmpDir with a duplicate name
        File tmpDir = new File(tmpFile + ".dir");
        Assert.assertFalse(tmpDir.exists()); // never true if tmpfile does it's job
        Assert.assertTrue(tmpDir.mkdirs());
        LOG.info("tmpDir:{}", tmpDir.getAbsolutePath());

        return tmpDir;
    }
    
    /**
     * Deletes recursively.
     * 
     * @param d
     * @return
     */
    public static boolean recursiveDelete(File d) {
        if (d.isDirectory()) {
            File children[] = d.listFiles();
            for (File f : children) {
                Assert.assertTrue("delete " + f.toString(), recursiveDelete(f));
            }
        }
        return d.delete();
    }
}
