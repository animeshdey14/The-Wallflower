Add-Type -TypeDefinition @"
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Runtime.InteropServices;
using System.Text;
public class VisibleWindow { public long handle; public string title; public string app; public int x; public int y; public int width; public int height; public bool active; public bool maximized; }
public static class WindowProbe {
  delegate bool EnumProc(IntPtr h, IntPtr l);
  [DllImport("user32.dll")] static extern bool EnumWindows(EnumProc cb, IntPtr lp);
  [DllImport("user32.dll")] static extern bool IsWindowVisible(IntPtr h);
  [DllImport("user32.dll")] static extern bool IsIconic(IntPtr h);
  [DllImport("user32.dll")] static extern bool IsZoomed(IntPtr h);
  [DllImport("user32.dll", CharSet=CharSet.Unicode)] static extern int GetWindowTextLength(IntPtr h);
  [DllImport("user32.dll", CharSet=CharSet.Unicode)] static extern int GetWindowText(IntPtr h, StringBuilder b, int n);
  [DllImport("user32.dll")] static extern bool GetWindowRect(IntPtr h, out RECT r);
  [DllImport("user32.dll")] static extern uint GetWindowThreadProcessId(IntPtr h, out uint pid);
  [DllImport("user32.dll")] static extern IntPtr GetForegroundWindow();
  [StructLayout(LayoutKind.Sequential)] struct RECT { public int Left,Top,Right,Bottom; }
  public static List<VisibleWindow> Read() { var list=new List<VisibleWindow>(); IntPtr fg=GetForegroundWindow(); EnumWindows((h,l)=>{ if(!IsWindowVisible(h)||IsIconic(h))return true; int n=GetWindowTextLength(h); if(n<1)return true; var b=new StringBuilder(n+1);GetWindowText(h,b,b.Capacity); string title=b.ToString().Trim(); if(title.Length==0)return true; RECT r;if(!GetWindowRect(h,out r))return true; int w=r.Right-r.Left,hh=r.Bottom-r.Top;if(w<120||hh<80)return true;uint pid;GetWindowThreadProcessId(h,out pid);string app="Unknown";try{app=Process.GetProcessById((int)pid).ProcessName;}catch{} list.Add(new VisibleWindow{handle=h.ToInt64(),title=title,app=app,x=r.Left,y=r.Top,width=w,height=hh,active=h==fg,maximized=IsZoomed(h)});return true;},IntPtr.Zero);return list; }
}
"@; $w=@([WindowProbe]::Read()); ConvertTo-Json -InputObject $w -Compress -Depth 4
