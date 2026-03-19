import { cn } from "@/lib/utils";

export function Badge({ className, variant = "default", ...props }) {
  const variants = {
    default: "bg-primary/15 text-primary border-primary/20",
    secondary: "bg-secondary text-secondary-foreground border-secondary",
    destructive: "bg-destructive/15 text-destructive border-destructive/20",
    success: "bg-emerald-500/15 text-emerald-400 border-emerald-500/20",
    outline: "text-foreground border-border",
  };

  return (
    <span
      className={cn(
        "inline-flex items-center rounded-md border px-2.5 py-0.5 text-xs font-medium transition-colors",
        variants[variant],
        className
      )}
      {...props}
    />
  );
}
