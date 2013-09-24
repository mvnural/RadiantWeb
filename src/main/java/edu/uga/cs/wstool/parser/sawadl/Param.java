/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Param.java .
 *   Created on : Nov 22, 2009 at 4:03:57 PM .
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
public class Param
{

    private List<Doc> docs;
    private List<Option> options;
    private Link link;
    private URI href;
    private String name;
    private String style;
    private String id;
    private String type;
    private String default1;
    private boolean required = false;
    private boolean repeation = false;
    private String fixed;
    private String path;
    // use for annotation
    private Element element;
    private String modelRefernece;

    public Param()
    {
    }

    public Param(List<Doc> docs, List<Option> options, Link link, URI href, String name, String style, String id, String type, String default1, String fixed, String path, Boolean required, Element e)
    {
        this.docs = docs;
        this.options = options;
        this.link = link;
        this.href = href;
        this.name = name;
        this.style = style;
        this.id = id;
        this.type = type;
        this.default1 = default1;
        this.fixed = fixed;
        this.path = path;
        this.required = required;
        this.element = e;
        Attribute attr = e.getAttribute("modelReference", WADLParser.getSAWADLNamespace());
        if(attr != null && attr.getValue() != null && !attr.getValue().isEmpty())
        {
        	this.modelRefernece = attr.getValue();
        }
        
    }

    public String getDefault1()
    {
        return default1;
    }

    public void setDefault1(String default1)
    {
        this.default1 = default1;
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> docs)
    {
        this.docs = docs;
    }

    public String getFixed()
    {
        return fixed;
    }

    public void setFixed(String fixed)
    {
        this.fixed = fixed;
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

    public Link getLink()
    {
        return link;
    }

    public void setLink(Link link)
    {
        this.link = link;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Option> getOptions()
    {
        return options;
    }

    public void setOptions(List<Option> options)
    {
        this.options = options;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isRepeation()
    {
        return repeation;
    }

    public void setRepeation(boolean repeation)
    {
        this.repeation = repeation;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

	public void setElement(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	public void setModelRefernece(String modelRefernece) {
		this.modelRefernece = modelRefernece;
	}

	public String getModelRefernece() {
		return modelRefernece;
	}
}
