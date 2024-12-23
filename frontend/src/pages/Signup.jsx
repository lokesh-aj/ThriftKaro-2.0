const handleFileInputChange = (e) => {
  const file = e.target.files[0];
  
  // Basic image validation
  if (file.size > 5000000) { // 5MB limit
    toast.error("File size too large! Please choose a file under 5MB");
    return;
  }

  const reader = new FileReader();

  reader.onload = () => {
    if (reader.readyState === 2) {
      // Compress image before setting it
      const img = new Image();
      img.src = reader.result;
      img.onload = () => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        
        // Set target width/height while maintaining aspect ratio
        const maxWidth = 800;
        const maxHeight = 800;
        let width = img.width;
        let height = img.height;
        
        if (width > height) {
          if (width > maxWidth) {
            height *= maxWidth / width;
            width = maxWidth;
          }
        } else {
          if (height > maxHeight) {
            width *= maxHeight / height;
            height = maxHeight;
          }
        }
        
        canvas.width = width;
        canvas.height = height;
        
        ctx.drawImage(img, 0, 0, width, height);
        
        // Get compressed image data
        const compressedDataUrl = canvas.toDataURL('image/jpeg', 0.7); // 0.7 is the quality (0-1)
        
        setAvatar(compressedDataUrl);
      };
    }
  };

  reader.readAsDataURL(file);
}; 