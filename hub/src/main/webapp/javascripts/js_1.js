/*
It is a application for event distribution to event n-consumers with m-sources.
Copyright (C) 2010 "Imran M Yousuf <imran@smartitengineering.com>"

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or any later
version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
function change()
{
    var className=document.getElementById("div1").className;
    if(className=="show")
        {
            document.getElementById("div1").className="hide";
            document.getElementById("div2").className="show";
            document.getElementById("content").className="hide";
        }
    else
        {
            document.getElementById("div1").className="show";
            document.getElementById("div2").className="hide";
        }

}


