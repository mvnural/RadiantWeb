/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Link.java .
 *   Created on : Nov 22, 2009 at 4:54:49 PM .
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 * 
 *  1. Redistributions of source code must retain the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *  2. Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of the University of Georgia nor the names
 *     of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 *  CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *  BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.uga.cs.wstool.parser.sawadl;

import java.util.List;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class Link
{

    private List<Doc> docs;
    private String resource_type;
    private String rel;
    private String rev;

    public Link(List<Doc> docs, String resource_type, String rel, String rev)
    {
        this.docs = docs;
        this.resource_type = resource_type;
        this.rel = rel;
        this.rev = rev;
    }

    public Link()
    {
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> docs)
    {
        this.docs = docs;
    }

    public String getRel()
    {
        return rel;
    }

    public void setRel(String rel)
    {
        this.rel = rel;
    }

    public String getResource_type()
    {
        return resource_type;
    }

    public void setResource_type(String resource_type)
    {
        this.resource_type = resource_type;
    }

    public String getRev()
    {
        return rev;
    }

    public void setRev(String rev)
    {
        this.rev = rev;
    }
}
