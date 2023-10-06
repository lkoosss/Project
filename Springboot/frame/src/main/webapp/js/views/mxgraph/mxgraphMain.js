var mxgraphMain = {

};

$(main);

function main() {
   mxgraphInit()
}

function mxgraphInit() {
   var mxGraphContainer = document.getElementById('mxGraphDiv')
   var clsGraph = new mxGraph(mxGraphContainer);
   new mxRubberband(clsGraph);
   var clsParent = clsGraph.getDefaultParent();

   clsGraph.getModel().beginUpdate();

   var v1 = clsGraph.insertVertex(clsParent, null, "첫번째", 20, 20, 70, 40);
   var v2 = clsGraph.insertVertex(clsParent, null, "두번째", 200, 150, 70, 40);
   var e1 = clsGraph.insertEdge(clsParent, null, "라인", v1, v2);

   clsGraph.getModel().endUpdate();
}