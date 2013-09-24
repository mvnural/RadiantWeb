/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Representation.java .
 *   Created on : Nov 22, 2009 at 4:40:18 PM .
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

import java.net.URI;
import java.util.List;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class Representation
{

    private List<Doc> docs;
    private List<Param> params;
    private String id;
    private String element;
    private String mediaType;
    private URI href;
    private String profile;

    public Representation()
    {
    }

    public Representation(List<Doc> docs, List<Param> params, String id, String element, String mediaType, URI href, String profile)
    {
        this.docs = docs;
        this.params = params;
        this.id = id;
        this.element = element;
        this.mediaType = mediaType;
        this.href = href;
        this.profile = profile;
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> docs)
    {
        this.docs = docs;
    }

    public String getElement()
    {
        return element;
    }

    public void setElement(String element)
    {
        this.element = element;
    }

    public URI getHref()
    {
        return href;
    }

    public void setHref(URI href)
    {
        this.href = href;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public List<Param> getParams()
    {
        return params;
    }

    public void setParams(List<Param> params)
    {
        this.params = params;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }
}
