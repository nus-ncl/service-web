
Figure.prototype.toggleBackground=function()
{
  if(this.highlight) {
    this.setBackgroundColor(new  Color(245,115,115));
  } else {
    this.setBackgroundColor(new  Color(115,245,115));
  }
  this.highlight = !this.highlight;
  this.toggleCountdown--;
  if (this.toggleCountdown == 0) {
    window.clearInterval(this.toggleTimer);
    this.setBackgroundColor(this.xsaveOldBackgroundColor);
  }
}


Figure.prototype.bgFlash = function() {
  if (this.toggleCountdown) {
    return;
  }
  this.toggleCountdown = 10;
  this.aOldBorder = this.border;
  try {
    this.xsaveOldBackgroundColor = this.getBackgroundColor();
  } catch (e) {
    this.xsaveOldBackgroundColor = null;
  }
  var oThis = this;
  var func = function(){oThis.toggleBackground();};
  this.highlight=false;
  this.toggleTimer = window.setInterval(func,100);
/*
  var aOldBorder = this.border;
  var aThis = this;
  var fff = function(aOld, aCount) {
    if (aCount == 0) {
      aThis.setBorder(aOld);
    } else {
      var brd = new Border();
      if(aCount%2==0) {
        brd.setColor(new Color(115,115,245));
      } else {
        brd.setColor(null);
      }
      aThis.setBorder(brd);
      this.setBackgroundColor(new  Color(245,115,115));
      setTimeout(function() { fff(aOld, aCount-1); }, 500);
    }
  }
  fff(aOldBorder, 10);
*/
}


Node.prototype.addAnnotation = function(aForWhat, aText, aAnnX, aAnnY) {
  if(!this.annotations) {
    this.annotations = new Array;
    this.oldSetPosition = this.setPosition;
    this.setPosition = function(aX, aY) {
      this.oldSetPosition(aX, aY);
      for(var i=0; i<this.annotations.length;i++) {
        var ann = this.annotations[i];
        ann.setPosition(aX+ann.DX, aY+ann.DY);
      }
    }

    this.oldOnDrag = this.onDrag;
    this.onDrag = function() {
      var oldX=this.getX();
      var oldY=this.getY();
      this.oldOnDrag();
      for(var i=0; i<this.annotations.length;i++) {
        var child = this.annotations[i];
        child.setPosition(child.getX()+this.getX()-oldX,child.getY()+this.getY()-oldY);
      }
    }
  }
  var ann = new Annotation(aText);
  ann.setBackgroundColor(null);
  ann.setDimension(this.width,20);
  ann.type = "-label-";
  if(aAnnX) {
    ann.DX = aAnnX;
    ann.DY = aAnnY;
  } else {
    ann.DX = 0;
    ann.DY = 0;
  }
  ann.TheTarget = this;
  ann.forWhat = aForWhat;
  ann.forWhat.annotation = ann;
  ann.onDrag=function(){
    Annotation.prototype.onDrag.call(this);
    this.DX = this.getX() - this.TheTarget.getX();
    this.DY = this.getY() - this.TheTarget.getY();
  };
  ann.onKeyDown=function(_3831,ctrl){
    if(_3831==46&&this.isDeleteable()==true){
      var i=0;
      if (this.forWhat.annotation == ann) {
        this.forWhat.annotation = null;
      }
      while(i<this.TheTarget.annotations.length) {
        var ann2 = this.TheTarget.annotations[i];
		if (ann2 == this) {
		  this.TheTarget.annotations.splice(i,1);
		} else {
		  i++;
		}
      }
      this.workflow.commandStack.execute(new CommandDelete(this));
    }
    if(ctrl){
      this.workflow.onKeyDown(_3831,ctrl);
    }
  };
  ann.doEdit=function(){
    var obj = this.html;
    var aThat = this;
    Element.hide(obj);
    workflow.setCurrentSelection(null);

    var forWhatType = ANN_FORWHAT_TYPES.UNKNOWN;
    if (aThat.forWhat.type == 'DiagramFigure') {
      if (aThat.forWhat.subtype == 'cloud_filled') {
        forWhatType = ANN_FORWHAT_TYPES.CLOUD;
      } else {
        forWhatType = ANN_FORWHAT_TYPES.NODE;
      }
    } else if (aThat.forWhat.type == 'MyInputPort') {
      forWhatType = ANN_FORWHAT_TYPES.PORT;
    }

    var modal = '<div id="'+obj.id+'_modal" style="position:fixed;left:0;top:0;right:0;bottom:0;z-index:99998;"></div>';
    //var textarea = '<div id="'+obj.id+'_editor" style="position: absolute; left:'+this.getX()+'; top:' + this.getY() +'"><textarea id="'+obj.id+'_edit" name="'+obj.id+'" rows="4" cols="60">'+obj.innerHTML+'</textarea>';
    var textarea = '<div id="'+obj.id+'_editor" style="position: fixed; z-index:99999; left:'+this.getX()+'; top:' + this.getY() +'">' + annTextToFormInputs(obj.innerHTML, obj.id, forWhatType);
    var button	 = '<div><input id="'+obj.id+'_save" type="button" value="SAVE" /> OR <input id="'+obj.id+'_cancel" type="button" value="CANCEL" /></div></div>';

    new Insertion.After(obj, modal+textarea+button);
    $(obj.id+'_edit').focus();
    $(obj.id+'_edit').select();

    function finishAnnEdit(aThat, obj, forWhatType, isSave) {
      if (isSave) {
        annText = annFormInputsToText(obj.id, forWhatType);
        aThat.setText(annText);
      }
      Element.remove(obj.id+'_editor');
      Element.remove(obj.id+'_modal');
      Element.show(obj);
    }
    Event.observe(obj.id+'_save', 'click', function(){ finishAnnEdit(aThat, obj, forWhatType, true); }, false);
    Event.observe(obj.id+'_cancel', 'click', function(){ finishAnnEdit(aThat, obj, forWhatType, false); }, false);
    Event.observe(obj.id+'_editor', 'keydown', function(event) { 
      var key = event.which || event.keyCode; 
      if (key==13) { finishAnnEdit(aThat, obj, forWhatType, true); }
      else if (key==27) { finishAnnEdit(aThat, obj, forWhatType, false); }
    }, false);
  }
  ann.onDoubleClick=function(){
    this.doEdit();
  }

  this.annotations.push(ann);
  workflow.addFigure(ann,this.getX() + ann.DX,this.getY() + ann.DY);
  return ann;
}

var ANN_FORWHAT_TYPES = {
  UNKNOWN: 0,
  NODE: 1,
  CLOUD: 2,
  PORT: 3
}

function annTextToFormInputs(text, id, forWhatType) {
  var forminputs = '<input type="text" id="'+id+'_edit" value="'+text+'">';
  if (forWhatType == ANN_FORWHAT_TYPES.NODE) {
    var nparts = text.split(";");
    var name = "";
    var install = [];
    var run = [];
    var box = "";
    var forward = false;
    for (i = 0; i < nparts.length; i++) {
      npart = nparts[i].trim();
      if (npart.startsWith("install.")) {
        install.push(npart.substr(8));
      } else if (npart.startsWith("run:")) {
        run.push(npart.substr(4).trim());
      } else if (npart.startsWith("box:")) {
        box = npart.substr(4).trim();
      } else if (npart.toLowerCase() == "forward") {
        forward = true;
      } else {  // name?
        if (name == "") {
          name = npart;
        } else {
          debug("Error: cannot recognize " + npart + " from annotation");
        }
      }
    }
    forminputs =
      '<div class="form-group><label for="'+id+'_edit">Name</label>&nbsp;<input type="text" id="'+id+'_edit" value="'+name+'" placeholder="node1"></div>' +
      '<div class="form-group><label for="'+id+'_install">Install</label>&nbsp;<input type="text" id="'+id+'_install" value="'+install.join(", ")+'" placeholder="splunk, web"></div>' +
      '<div class="form-group><label for="'+id+'_run">Run</label>&nbsp;<input type="text" id="'+id+'_run" value="'+run.join(", ")+'" placeholder="ls, hostname"></div>' +
      '<div class="form-group><label for="'+id+'_box">Box</label>&nbsp;<input type="text" id="'+id+'_box" value="'+box+'" placeholder="bento/ubuntu-16.04"></div>' +
      '<div class="form-group><label for="'+id+'_forward">Forward</label>&nbsp;<input type="checkbox" id="'+id+'_forward"' + (forward ? 'checked="checked"' : '') + '></div>';
  } else if (forWhatType == ANN_FORWHAT_TYPES.CLOUD) {
    var nparts = text.split(";");
    var name = "";
    var reserve = "";
    for (i = 0; i < nparts.length; i++) {
      npart = nparts[i].trim();
      if (npart.startsWith("reserve:")) {
        reserve = npart.substr(8).trim();
      } else {  // name?
        if (name == "") {
          name = npart;
        } else {
          debug("Error: cannot recognize " + npart + " from annotation");
        }
      }
    }
    forminputs =
      '<div class="form-group><label for="'+id+'_edit">Name</label>&nbsp;<input type="text" id="'+id+'_edit" value="'+name+'" placeholder="exp1.teamname"></div>' +
      '<div class="form-group><label for="'+id+'_reserve">Reserve</label>&nbsp;<input type="text" id="'+id+'_reserve" value="'+reserve+'" placeholder="pc18h, pc4a"></div>';
  } else if (forWhatType == ANN_FORWHAT_TYPES.PORT) {
    var nparts = text.split(";");
    var ipaddress = "";
    var bridge = false;
    var nat = [];
    for (i = 0; i < nparts.length; i++) {
      npart = nparts[i].trim();
      if (npart.startsWith("NAT:")) {
        nat.push(npart.substr(4));
      } else {
        var nbridge = npart.indexOf("bridge");
        if (nbridge >= 0) {
          bridge = true;
          ipaddress = npart.substring(0, nbridge).trim();
        } else {
          ipaddress = npart;
        }
      }
    }
    forminputs =
      '<div class="form-group><label for="'+id+'_edit">IP Address</label>&nbsp;<input type="text" id="'+id+'_edit" value="'+ipaddress+'" placeholder="172.16.1.1"></div>' +
      '<div class="form-group><label for="'+id+'_bridge">Bridge</label>&nbsp;<input type="checkbox" id="'+id+'_bridge"' + (bridge ? 'checked="checked"' : '') + '></div>' +
      '<div class="form-group><label for="'+id+'_nat">NAT</label>&nbsp;<input type="text" id="'+id+'_nat" value="'+nat+'" placeholder="-A PREROUTING -p tcp -d 172.16.20.2 --dport 80 -j DNAT --to-destination 172.16.2.2"></div>';
  }
  return forminputs;
}

function annFormInputsToText(id, forWhatType) {
  var ann = [];
  if (forWhatType == ANN_FORWHAT_TYPES.NODE) {
    var name = $F(id+'_edit').trim();
    var install = $F(id+'_install').split(",");
    var run = $F(id+'_run').split(",");
    var box = $F(id+'_box').trim();
    var forward = $(id+'_forward').checked;
    ann.push(name);
    for (i = 0; i < install.length; i++) {
      part = install[i].trim();
      if (part.length > 0) {
        ann.push("install." + part);
      }
    }
    for (i = 0; i < run.length; i++) {
      part = run[i].trim();
      if (part.length > 0) {
        ann.push("run: " + part);
      }
    }
    if (box.length > 0) {
      ann.push("box: " + box);
    }
    if (forward == true) {
      ann.push("forward");
    }
  } else if (forWhatType == ANN_FORWHAT_TYPES.CLOUD) {
    var name = $F(id+'_edit').trim();
    var reserve = $F(id+'_reserve').trim();
    ann.push(name);
    if (reserve.length > 0) {
      ann.push("reserve: " + reserve);
    }
  } else if (forWhatType == ANN_FORWHAT_TYPES.PORT) {
    var ipaddress = $F(id+'_edit').trim();
    var bridge = $(id+'_bridge').checked;
    var nat = $F(id+'_nat').split(",");
    if (bridge == true) {
      ann.push(ipaddress + " bridge");
    } else {
      ann.push(ipaddress);
    }
    for (i = 0; i < nat.length; i++) {
      part = nat[i].trim();
      if (part.length > 0) {
        ann.push("NAT: " + part);
      }
    }
  } else {
    ann.push($F(id+'_edit').trim());
  }
  return ann.join("; ");
}

DiagramFigure=function(){
  this.type = "DiagramFigure";
  ImageFigure.call(this);
  this.inputPort=null;
};

DiagramFigure=function(w, h){
  this.type = "DiagramFigure";
  ImageFigure.call(this, null, w, h);
  this.inputPort=null;
};

DiagramFigure.prototype=new ImageFigure;
DiagramFigure.prototype.type="DiagramFigure";
DiagramFigure.prototype.setPic = function(aPic) {
  this.pic = aPic;
  this.setImage(aPic);
}
/*
DiagramFigure.prototype.setPosition=function(aX, aY){
  ImageFigure.prototype.setPosition.call(this, aX, aY);
  this.annotation.setPosition(aX+this.annotation.DX, aY+this.annotation.DY, true);
};
*/
/*
DiagramFigure.prototype.onDrag=function(){
var oldX=this.getX();
var oldY=this.getY();
var child = this.annotation;
ImageFigure.prototype.onDrag.call(this);
child.setPosition(child.getX()+this.getX()-oldX,child.getY()+this.getY()-oldY);
}
*/

DiagramFigure.prototype.toJSON=function() {
  var js = new Object;
  var ports = this.getPorts();
  var js_ports = new Array;
  var js_annotations = new Array;
  debug("Figure start"); 
  if (typeof(this.annotations) != "undefined") {
    debug("annotations len: " + this.annotations.length);
    for (var j=0; j<this.annotations.length; j++) {
      var js_ann = new Object;
      var ann = this.annotations[j];
      js_ann['text'] = ann.getText();
      js_ann['x'] = ann.getX();
      js_ann['y'] = ann.getY();
      js_ann['for'] = ann.forWhat.getId();
      js_annotations.push(js_ann);
    }
  }

  for (var j=0; j<ports.length; j++) {
    js_ports.push(ports[j].toJSON());
  }

  js['id'] = this.getId();
  js['type'] = this.type;
  js['subtype'] = this.subtype;
  js['pic'] = this.pic;
  js['x'] = this.getX();
  js['y'] = this.getY();
  js['width'] = this.getWidth();
  js['height'] = this.getHeight();
  js['ports'] = js_ports;
  js['annotations'] = js_annotations;
  debug("Figure end"); 
  return js;
}

DiagramFigure.prototype.onSelectionChanged=function(newSel){
  try { 
    if(newSel.type == "-label-") {
      if(newSel.forWhat) {
        newSel.forWhat.bgFlash();
      }
    }
  } catch (err) {
    // if properties do not exist, do nothing
  }
}

DiagramFigure.prototype.onDoubleClick=function(){
  if(!this.annotation) {
    this.addAnnotation(this, "Description for the figure", 1, this.height + 5);
    this.annotation.doEdit();
    this.bgFlash();
  } else {
    this.annotation.bgFlash();
  }
}

DiagramFigure.prototype.setWorkflow=function(_3a5c){
ImageFigure.prototype.setWorkflow.call(this,_3a5c);
if(_3a5c!=null&&this.inputPort==null){
this.inputPort=new MyInputPort();
this.inputPort.setWorkflow(_3a5c);
this.addPort(this.inputPort,0,this.height/2);
this.inputPort2=new MyInputPort();
this.inputPort2.setWorkflow(_3a5c);
this.addPort(this.inputPort2,this.width/2,0);
this.inputPort3=new MyInputPort();
this.inputPort3.setWorkflow(_3a5c);
this.addPort(this.inputPort3,this.width,this.height/2);
this.inputPort4=new MyInputPort();
this.inputPort4.setWorkflow(_3a5c);
this.addPort(this.inputPort4,this.width/2,this.height);

this.inputPort5=new MyInputPort();
this.inputPort5.setWorkflow(_3a5c);
this.addPort(this.inputPort5,this.width,this.height);
this.inputPort6=new MyInputPort();
this.inputPort6.setWorkflow(_3a5c);
this.addPort(this.inputPort6,0,this.height);
this.inputPort7=new MyInputPort();
this.inputPort7.setWorkflow(_3a5c);
this.addPort(this.inputPort7,this.width,0);
this.inputPort8=new MyInputPort();
this.inputPort8.setWorkflow(_3a5c);
this.addPort(this.inputPort8,0,0);

this.workflow.addSelectionListener(this);
//workflow.addFigure(this.annotation,this.getX(),this.getY());
};
};
DiagramFigure.prototype.onKeyDown=function(_3831,ctrl){
	console.log(_3831);
	console.log(ctrl);
    /*if(_3831==46&&this.isDeleteable()==true){
      var i=0;
      if (this.forWhat.annotation == ann) {
        this.forWhat.annotation = null;
      }
      while(i<this.TheTarget.annotations.length) {
        var ann2 = this.TheTarget.annotations[i];
		if (ann2 == this) {
		  this.TheTarget.annotations.splice(i,1);
		} else {
		  i++;
		}
      }
      this.workflow.commandStack.execute(new CommandDelete(this));
    }
    if(ctrl){
      this.workflow.onKeyDown(_3831,ctrl);
    }*/
  };
DiagramFigure.prototype.getContextMenu=function(){
var menu=new Menu();

/*
var oThis=this;
menu.appendMenuItem(new MenuItem("NULL Router",null,function(){
oThis.setRouter(null);
}));
menu.appendMenuItem(new MenuItem("Manhatten Router",null,function(){
oThis.setRouter(new ManhattanConnectionRouter());
}));
menu.appendMenuItem(new MenuItem("Bezier Router",null,function(){
oThis.setRouter(new BezierConnectionRouter());
}));
menu.appendMenuItem(new MenuItem("Fan Router",null,function(){
oThis.setRouter(new FanConnectionRouter());
}));
*/
var oThis=this;
menu.appendMenuItem(new MenuItem("Delete",null,function(){

workflow.commandStack.execute(new CommandDelete(oThis));
workflow.commandStack.execute(new CommandDelete(oThis.annotation));
if(oThis.inputPort.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort.annotation));
if(oThis.inputPort2.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort2.annotation));
if(oThis.inputPort3.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort3.annotation));
if(oThis.inputPort4.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort4.annotation));
if(oThis.inputPort5.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort5.annotation));
if(oThis.inputPort6.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort6.annotation));
if(oThis.inputPort7.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort7.annotation));
if(oThis.inputPort8.annotation != undefined)
	workflow.commandStack.execute(new CommandDelete(oThis.inputPort8.annotation));
//oThis.getParent().removePort(oThis);
}));
return menu;
};
