/*
 * Copyright (c) 2009 Srikalyan Swayampakula.. All rights reserved.
 * 
 *   Author : Srikalyan Swayampakula. .
 *   Name of the File : Application.java .
 *   Created on : Nov 22, 2009 at 3:20:25 PM .
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
import java.util.Map;

import org.jdom.Element;

/**
 *
 * @author Srikalyan Swayampakula.
 */
public class Application
{

    private List<Resources> resources;
    private List<Doc> docs;
    private Grammar grammar;
    private Map<String, String> namespaces;
    // use for annotation
    private Element element;
    
    public Application()
    {
    }

    public Application(List<Resources> resources, List<Doc> docs, Grammar grammar, Map<String, String> namespaces, Element e)
    {
        this.resources = resources;
        this.docs = docs;
        this.grammar = grammar;
        this.namespaces = namespaces;
        this.setElement(e);
    }

    public List<Doc> getDocs()
    {
        return docs;
    }

    public void setDocs(List<Doc> doc)
    {
        this.docs = doc;
    }

    public Grammar getGrammar()
    {
        return grammar;
    }

    public void setGrammar(Grammar grammar)
    {
        this.grammar = grammar;
    }

    public Map<String, String> getNamespaces()
    {
        return namespaces;
    }

    public void setNamespaces(Map<String, String> namespaces)
    {
        this.namespaces = namespaces;
    }

    public List<Resources> getResources()
    {
        return resources;
    }

    public void setResources(List<Resources> resources)
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
