package com.shopping.core.security;

import java.util.Map;

public interface SecurityManager
{
  Map<String, String> loadUrlAuthorities();
}
