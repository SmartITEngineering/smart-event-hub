/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function change()
{
    var className=document.getElementById("div1").className;
    if(className=="show")
        {
            document.getElementById("div1").className="hide";
            document.getElementById("div2").className="show";
        }
    else
        {
            document.getElementById("div1").className="show";
            document.getElementById("div2").className="hide";
        }

}


