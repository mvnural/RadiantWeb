/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Resource.java .
 *   Created on : Nov 22, 2009 at 3:22:09 PM .
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

import org.jdom.Element;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class Resource
{

    public static final String QUERY_TYPE_DEFAULT = "application/x-www-form-urlencoded";
    private List<Doc> docs;
    private List<Param> params;
    private List<Method> methods;
    private List<Resource> resources;
    private String id;
    private String queryType;
    private String path;
    // use for annotation
    private Element element;

    public Resource()
    {
    }

    public Resource(List<Doc> docs, List<Param> params, List<Method> methods, List<Resource> resources, String id, String queryType, String path, Element e)
    {
        this.docs = docs;
        this.params = params;
        this.methods = methods;
        this.resources = resources;
        this.id = id;
        if (queryType != null)
        {
            this.queryType = queryType;
        }
        else
        {
            queryType = QUERY_TYPE_DEFAULT;
        }
        this.path = path;
        this.setElement(e);
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> docs)
    {
        this.docs = docs;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public List<Method> getMethods()
    {
        return methods;
    }

    public void setMethods(List<Method> methods)
    {
        this.methods = methods;
    }

    public List<Param> getParams()
    {
        return params;
    }

    public void setParams(List<Param> params)
    {
        this.params = params;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getQueryType()
    {
        return queryType;
    }

    public void setQueryType(String queryType)
    {
        this.queryType = queryType;
    }

    public List<Resource> getResources()
    {
        return resources;
    }

    public void setResources(List<Resource> resources)
    {
        this.resources = resources;
    }

	public void setElement(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}
}
