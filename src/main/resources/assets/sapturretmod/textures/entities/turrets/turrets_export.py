import os
from krita import *

def toggleLayerVisibility(doc, visible, *layerPath):
    nodes = doc.topLevelNodes()
    currNode = None
    for layer in layerPath:
        if nodes != None:
            for node in nodes:
                if node.name() == layer:
                    currNode = node
                    nodes = node.childNodes()
                    break;
    
    if currNode != None:
        currNode.setVisible(visible)

class Turret:
    nameset = set()
    layer1set = set()
    layer2set = set()
    
    def __init__(self, name, layer1, layer2 = None, glowmap = False):
        self.name = name
        self.layer1 = self.getLayerHierarchy(layer1)
        self.layer2 = self.getLayerHierarchy(layer2)
        self.isGlowmap = glowmap
        
        Turret.nameset.add(self.name)
        Turret.layer1set.add(self.layer1)
        
        if self.layer2 != None:
            Turret.layer2set.add(self.layer2)
        
    def getLayerHierarchy(self, layer):
        if layer == None:
            return None
        
        layers = [self.name]
        layers.extend(layer.split('/'))
        
        return tuple(layers)
    
    def toggleLayers(self, doc):
        for name in Turret.nameset:
            toggleLayerVisibility(doc, name == self.name, name)
        for layer in Turret.layer1set:
            toggleLayerVisibility(doc, False, *layer)
        for layer in Turret.layer2set:
            toggleLayerVisibility(doc, False, *layer)
            
        toggleLayerVisibility(doc, True, *self.layer1)
        if self.layer2 != None:
            toggleLayerVisibility(doc, True, *self.layer2)

turrets = {
    "t1_crossbow/cobblestone_oak.png": Turret("t1_crossbow",      "base/cobble", "throat & frame/oak"),
    "t1_crossbow/cobblestone_spruce.png": Turret("t1_crossbow",   "base/cobble", "throat & frame/spruce"),
    "t1_crossbow/cobblestone_acacia.png": Turret("t1_crossbow",   "base/cobble", "throat & frame/acacia"),
    "t1_crossbow/cobblestone_birch.png": Turret("t1_crossbow",    "base/cobble", "throat & frame/birch"),
    "t1_crossbow/cobblestone_dark_oak.png": Turret("t1_crossbow", "base/cobble", "throat & frame/dark oak"),
    "t1_crossbow/cobblestone_jungle.png": Turret("t1_crossbow",   "base/cobble", "throat & frame/jungle"),
    "t1_crossbow/mossy_cobblestone_oak.png": Turret("t1_crossbow",      "base/mossy cobble", "throat & frame/oak"),
    "t1_crossbow/mossy_cobblestone_spruce.png": Turret("t1_crossbow",   "base/mossy cobble", "throat & frame/spruce"),
    "t1_crossbow/mossy_cobblestone_acacia.png": Turret("t1_crossbow",   "base/mossy cobble", "throat & frame/acacia"),
    "t1_crossbow/mossy_cobblestone_birch.png": Turret("t1_crossbow",    "base/mossy cobble", "throat & frame/birch"),
    "t1_crossbow/mossy_cobblestone_dark_oak.png": Turret("t1_crossbow", "base/mossy cobble", "throat & frame/dark oak"),
    "t1_crossbow/mossy_cobblestone_jungle.png": Turret("t1_crossbow",   "base/mossy cobble", "throat & frame/jungle"),
    "t1_crossbow/diorite_oak.png": Turret("t1_crossbow",      "base/diorite", "throat & frame/oak"),
    "t1_crossbow/diorite_spruce.png": Turret("t1_crossbow",   "base/diorite", "throat & frame/spruce"),
    "t1_crossbow/diorite_acacia.png": Turret("t1_crossbow",   "base/diorite", "throat & frame/acacia"),
    "t1_crossbow/diorite_birch.png": Turret("t1_crossbow",    "base/diorite", "throat & frame/birch"),
    "t1_crossbow/diorite_dark_oak.png": Turret("t1_crossbow", "base/diorite", "throat & frame/dark oak"),
    "t1_crossbow/diorite_jungle.png": Turret("t1_crossbow",   "base/diorite", "throat & frame/jungle"),
    "t1_crossbow/andesite_oak.png": Turret("t1_crossbow",      "base/andesite", "throat & frame/oak"),
    "t1_crossbow/andesite_spruce.png": Turret("t1_crossbow",   "base/andesite", "throat & frame/spruce"),
    "t1_crossbow/andesite_acacia.png": Turret("t1_crossbow",   "base/andesite", "throat & frame/acacia"),
    "t1_crossbow/andesite_birch.png": Turret("t1_crossbow",    "base/andesite", "throat & frame/birch"),
    "t1_crossbow/andesite_dark_oak.png": Turret("t1_crossbow", "base/andesite", "throat & frame/dark oak"),
    "t1_crossbow/andesite_jungle.png": Turret("t1_crossbow",   "base/andesite", "throat & frame/jungle"),
    "t1_crossbow/granite_oak.png": Turret("t1_crossbow",      "base/granite", "throat & frame/oak"),
    "t1_crossbow/granite_spruce.png": Turret("t1_crossbow",   "base/granite", "throat & frame/spruce"),
    "t1_crossbow/granite_acacia.png": Turret("t1_crossbow",   "base/granite", "throat & frame/acacia"),
    "t1_crossbow/granite_birch.png": Turret("t1_crossbow",    "base/granite", "throat & frame/birch"),
    "t1_crossbow/granite_dark_oak.png": Turret("t1_crossbow", "base/granite", "throat & frame/dark oak"),
    "t1_crossbow/granite_jungle.png": Turret("t1_crossbow",   "base/granite", "throat & frame/jungle"),
    "t1_crossbow/glow.png": Turret("t1_crossbow", "glow mask", glowmap=True)
}

app = Krita.instance()
currDoc = app.activeDocument()
currDoc.setBatchmode(True)

print(Turret.layer1set)

for location, turret in turrets.items():
    turret.toggleLayers(currDoc)
    currDoc.refreshProjection()
    currDoc.waitForDone()
    
    savePath = os.path.join(os.path.split(currDoc.fileName())[0], location)
    
    expParams = InfoObject()
    expParams.setProperty("alpha", not turret.isGlowmap)
    expParams.setProperty("compression", 0)
    expParams.setProperty("forceSRGB", True)
    expParams.setProperty("indexed", turret.isGlowmap)
    expParams.setProperty("interlaced", False)
    expParams.setProperty("saveSRGBProfile", False)
    
    if turret.isGlowmap:
        expParams.setProperty("transparencyFillcolor", 0x0)
    
    currDoc.exportImage(savePath, expParams)
    
list(turrets.values())[0].toggleLayers(currDoc)
currDoc.refreshProjection()
currDoc.waitForDone()

currDoc.setBatchmode(False)