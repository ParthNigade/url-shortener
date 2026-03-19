import { cn } from "@/lib/utils";

export function Input({ className, type, ...props }) {
  return (
    <input
      type={type}
      className={cn(
        "flex h-10 w-full rounded-lg border border-input bg-background px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring transition-colors",
        className
      )}
      {...props}
    />
  );
}
