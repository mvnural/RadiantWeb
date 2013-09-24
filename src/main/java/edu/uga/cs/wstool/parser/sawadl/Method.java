/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 *
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Method.java .
 *   Created on : Nov 22, 2009 at 4:04:55 PM .
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

import org.jdom.Attribute;
import org.jdom.Element;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class Method
{
	private List<Doc> docs;
    private Request request;
    private List<Response> response;
    private String id;
    private String name;
    private URI href;
    // use for annotation
    private Element element;
    private String modelReference;

    public Method()
    {
    }

    public Method(List<Doc> docs, Request request, List<Response> response, String id, String name, URI href, Element e)
    {
        this.docs = docs;
        this.request = request;
        this.response = response;
        this.id = id;
        this.name = name;
        this.href = href;
        this.element = e;
        Attribute attr = e.getAttribute("modelReference", WADLParser.getSAWADLNamespace());
        if(attr != null && attr.getValue() != null && !attr.getValue().isEmpty())
        {
        	this.modelReference = attr.getValue();
        }
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> docs)
    {
        this.docs = docs;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Request getRequest()
    {
        return request;
    }

    public void setRequest(Request request)
    {
        this.request = request;
    }

    public List<Response> getResponse()
    {
        return response;
    }

    public void setResponse(List<Response> response)
    {
        this.response = response;
    }

	public void setElement(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setModelReference(String modelReference) {
		this.modelReference = modelReference;
	}

	public String getModelReference() {
		return modelReference;
	}
}
